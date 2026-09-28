package com.dataverse.rentals.service.impl;

import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperLecturaMedidor;
import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.lecturaMedidor.request.LecturaMedidorRequest;
import com.dataverse.rentals.model.lecturaMedidor.response.LecturaMedidorDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceLecturaMedidor;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplLecturaMedidor implements ServiceLecturaMedidor {

    private final MapperLecturaMedidor mapper;
    private final MapperPeriodoFacturacion mapperPeriodo;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<LecturaMedidorDTO>> listarLecturas() {
        try {
            List<LecturaMedidorDTO> data = mapper.listarLecturas();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> insertUpdateLectura(LecturaMedidorRequest args) {
        try {
            // La fecha de lectura tiene que caer dentro del mismo mes que el
            // periodo que se esta reportando; de lo contrario el periodo deja
            // de significar "cuando se tomo la lectura" (p. ej. periodo
            // 2026-09 con fecha_lectura en diciembre no tiene sentido).
            if (args.getPeriodo() != null && args.getFechaLectura() != null) {
                YearMonth periodo = YearMonth.parse(args.getPeriodo());
                YearMonth mesLectura = YearMonth.from(args.getFechaLectura());
                if (!periodo.equals(mesLectura)) {
                    return new ResponseDTO<>(true, "La fecha de lectura debe caer dentro del mes del periodo (" + args.getPeriodo() + ").");
                }
            }

            if (args.getIdLectura() == null) {
                Integer idUsuario = audit.usuarioActual();
                // lectura_medidor referencia periodo_facturacion(periodo); si es la
                // primera lectura del mes, el periodo todavia no existe.
                mapperPeriodo.asegurarPeriodo(args.getPeriodo(), idUsuario);
                mapper.insertarLectura(args, idUsuario, audit.ipActual());
                return new ResponseDTO<>(args.getIdLectura(), false, "Lectura registrada correctamente.");
            }
            mapper.actualizarLectura(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdLectura(), false, "Lectura actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarLectura(LecturaMedidorRequest args) {
        try {
            int filas = mapper.eliminarLectura(args.getIdLectura(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró la lectura a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Lectura eliminada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

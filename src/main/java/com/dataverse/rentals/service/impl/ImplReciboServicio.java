package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.mapper.MapperReciboServicio;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.reciboServicio.request.ReciboServicioRequest;
import com.dataverse.rentals.model.reciboServicio.response.ReciboServicioDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceReciboServicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplReciboServicio implements ServiceReciboServicio {

    private final MapperReciboServicio mapper;
    private final MapperPeriodoFacturacion mapperPeriodo;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<ReciboServicioDTO>> listarRecibos() {
        try {
            List<ReciboServicioDTO> data = mapper.listarRecibos();
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
    public ResponseDTO<Integer> insertUpdateRecibo(ReciboServicioRequest args) {
        try {
            if (args.getPeriodo() == null || args.getPeriodo().isBlank()) {
                return new ResponseDTO<>(true, "Debe indicar el periodo del recibo (formato AAAA-MM).");
            }
            if (args.getMontoTotal() == null || args.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseDTO<>(true, "El monto del recibo debe ser mayor a cero.");
            }

            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            // El recibo puede llegar antes de que existan lecturas de ese mes
            // (p. ej. la empresa de luz factura antes de que se terminen de
            // tomar las lecturas), asi que el periodo se crea aqui si hace
            // falta, igual que en LecturaMedidor, Prorrateo y Liquidacion.
            mapperPeriodo.asegurarPeriodo(args.getPeriodo(), idUsuario);

            if (args.getIdRecibo() == null) {
                mapper.insertarRecibo(args, idUsuario, ip);
                return new ResponseDTO<>(args.getIdRecibo(), false, "Recibo registrado correctamente.");
            }
            mapper.actualizarRecibo(args, idUsuario, ip);
            return new ResponseDTO<>(args.getIdRecibo(), false, "Recibo actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarRecibo(ReciboServicioRequest args) {
        try {
            if (mapper.contarProrrateosDeRecibo(args.getIdRecibo()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar el recibo: hay prorrateos que lo referencian.");
            }
            int filas = mapper.eliminarRecibo(args.getIdRecibo(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el recibo a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Recibo eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

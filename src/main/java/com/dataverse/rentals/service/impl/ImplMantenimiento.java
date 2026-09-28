package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperMantenimiento;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.mantenimiento.request.MantenimientoRequest;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceMantenimiento;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplMantenimiento implements ServiceMantenimiento {

    private final MapperMantenimiento mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<MantenimientoDTO>> listarMantenimientos() {
        try {
            List<MantenimientoDTO> data = mapper.listarMantenimientos();
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
    public ResponseDTO<Integer> insertUpdateMantenimiento(MantenimientoRequest args) {
        try {
            // El monto total no se toma del cliente: se calcula aca, para que
            // nunca pueda quedar desalineado con el desglose de materiales y
            // mano de obra (lo que exige chk_mantenimiento_desglose en la base).
            BigDecimal materiales = args.getMontoMateriales() != null ? args.getMontoMateriales() : BigDecimal.ZERO;
            BigDecimal manoObra = args.getMontoManoObra() != null ? args.getMontoManoObra() : BigDecimal.ZERO;
            args.setMontoMateriales(materiales);
            args.setMontoManoObra(manoObra);
            args.setMonto(materiales.add(manoObra));

            if (args.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseDTO<>(true, "El monto de materiales y/o mano de obra debe ser mayor a cero.");
            }

            if (args.getIdMantenimiento() == null) {
                mapper.insertarMantenimiento(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdMantenimiento(), false, "Mantenimiento registrado correctamente.");
            }
            mapper.actualizarMantenimiento(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdMantenimiento(), false, "Mantenimiento actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarMantenimiento(MantenimientoRequest args) {
        try {
            int filas = mapper.eliminarMantenimiento(args.getIdMantenimiento(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el mantenimiento a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Mantenimiento eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperDetalleLiquidacion;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.detalleLiquidacion.request.DetalleLiquidacionRequest;
import com.dataverse.rentals.model.detalleLiquidacion.response.DetalleLiquidacionDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceDetalleLiquidacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplDetalleLiquidacion implements ServiceDetalleLiquidacion {

    private final MapperDetalleLiquidacion mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<DetalleLiquidacionDTO>> listarPorLiquidacion(Integer idLiquidacion) {
        try {
            List<DetalleLiquidacionDTO> data = mapper.listarPorLiquidacion(idLiquidacion);
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
    public ResponseDTO<Integer> insertUpdateDetalle(DetalleLiquidacionRequest args) {
        try {
            if (args.getTipoConcepto() == null || args.getTipoConcepto().isBlank()) {
                args.setTipoConcepto("OTRO");
            }
            if (args.getIdDetalle() == null) {
                mapper.insertarDetalle(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdDetalle(), false, "Concepto registrado correctamente.");
            }
            mapper.actualizarDetalle(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdDetalle(), false, "Concepto actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarDetalle(DetalleLiquidacionRequest args) {
        try {
            int filas = mapper.eliminarDetalle(args.getIdDetalle(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el concepto a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Concepto eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

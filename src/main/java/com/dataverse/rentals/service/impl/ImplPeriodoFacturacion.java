package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.periodoFacturacion.request.PeriodoFacturacionRequest;
import com.dataverse.rentals.model.periodoFacturacion.response.PeriodoFacturacionDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServicePeriodoFacturacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * periodo_facturacion no tiene borrado logico: los periodos son un catalogo
 * de estado (EN_PROGRESO / COMPLETO / LIQUIDADO), no un registro de negocio
 * que se pueda eliminar sin dejar huerfanas las lecturas, recibos, prorrateos
 * y liquidaciones que ya lo referencian.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ImplPeriodoFacturacion implements ServicePeriodoFacturacion {

    private final MapperPeriodoFacturacion mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<PeriodoFacturacionDTO>> listarPeriodos() {
        try {
            List<PeriodoFacturacionDTO> data = mapper.listarPeriodos();
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
    public ResponseDTO<Integer> insertUpdatePeriodo(PeriodoFacturacionRequest args) {
        try {
            if (args.getPeriodo() == null || args.getPeriodo().isBlank()) {
                return new ResponseDTO<>(true, "Debe indicar el periodo (formato AAAA-MM).");
            }
            if (args.getIdPeriodo() == null) {
                if (args.getEstado() == null || args.getEstado().isBlank()) {
                    args.setEstado("EN_PROGRESO");
                }
                mapper.insertarPeriodo(args, audit.usuarioActual());
                return new ResponseDTO<>(args.getIdPeriodo(), false, "Periodo registrado correctamente.");
            }
            mapper.actualizarPeriodo(args);
            return new ResponseDTO<>(args.getIdPeriodo(), false, "Periodo actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

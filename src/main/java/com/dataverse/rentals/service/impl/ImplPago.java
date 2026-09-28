package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.dataverse.rentals.mapper.MapperPago;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.pago.request.PagoRequest;
import com.dataverse.rentals.model.pago.response.PagoDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServicePago;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplPago implements ServicePago {

    private final MapperPago mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<PagoDTO>> listarPagos() {
        try {
            List<PagoDTO> data = mapper.listarPagos();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * Registra o actualiza un pago y recalcula el estado de la liquidación
     * afectada (PENDIENTE / PARCIAL / PAGADO) dentro de la misma transacción.
     * Valida RN-02 (voucher o evidencia) y que el monto no exceda el saldo (CU-07).
     */
    @Override
    @Transactional
    public ResponseDTO<Integer> insertUpdatePago(PagoRequest args) {
        try {
            normalizarEvidencia(args);
            String error = validar(args);
            if (error != null) {
                return new ResponseDTO<>(true, error);
            }

            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            if (args.getIdPago() == null) {
                mapper.insertarPago(args, idUsuario, ip);
                mapper.recalcularEstadoLiquidacion(args.getIdLiquidacion(), idUsuario, ip);
                return new ResponseDTO<>(args.getIdPago(), false, "Pago registrado correctamente.");
            }

            Integer liquidacionAnterior = mapper.obtenerIdLiquidacionDePago(args.getIdPago());
            int filas = mapper.actualizarPago(args, idUsuario, ip);
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el pago a actualizar.");
            }
            mapper.recalcularEstadoLiquidacion(args.getIdLiquidacion(), idUsuario, ip);
            if (liquidacionAnterior != null && !liquidacionAnterior.equals(args.getIdLiquidacion())) {
                mapper.recalcularEstadoLiquidacion(liquidacionAnterior, idUsuario, ip);
            }
            return new ResponseDTO<>(args.getIdPago(), false, "Pago actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            marcarRollback();
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Integer> eliminarPago(PagoRequest args) {
        try {
            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            Integer idLiquidacion = mapper.obtenerIdLiquidacionDePago(args.getIdPago());
            int filas = mapper.eliminarPago(args.getIdPago(), idUsuario, ip);
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el pago a eliminar.");
            }
            if (idLiquidacion != null) {
                mapper.recalcularEstadoLiquidacion(idLiquidacion, idUsuario, ip);
            }
            return new ResponseDTO<>(filas, false, "Pago eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            marcarRollback();
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    private void normalizarEvidencia(PagoRequest args) {
        if (esVacio(args.getNumeroVoucher())) {
            args.setNumeroVoucher(null);
        }
        if (esVacio(args.getRutaEvidencia())) {
            args.setRutaEvidencia(null);
        }
    }

    private String validar(PagoRequest args) {
        if (args.getIdLiquidacion() == null) {
            return "Debe indicar la liquidación que se está pagando.";
        }
        if (args.getMontoPagado() == null || args.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
            return "El monto pagado debe ser mayor a cero.";
        }
        if (args.getNumeroVoucher() == null && args.getRutaEvidencia() == null) {
            return "Todo pago debe tener número de voucher o evidencia adjunta (RN-02).";
        }

        String estado = mapper.obtenerEstadoLiquidacion(args.getIdLiquidacion());
        if (estado == null) {
            return "La liquidación indicada no existe.";
        }
        if ("BORRADOR".equals(estado)) {
            return "No se pueden registrar pagos sobre una liquidación en borrador.";
        }

        BigDecimal total = mapper.obtenerMontoTotalLiquidacion(args.getIdLiquidacion());
        int excluir = args.getIdPago() == null ? 0 : args.getIdPago();
        BigDecimal pagado = mapper.totalPagadoLiquidacion(args.getIdLiquidacion(), excluir);
        BigDecimal saldo = total.subtract(pagado);
        if (args.getMontoPagado().compareTo(saldo) > 0) {
            return "El monto pagado excede el saldo pendiente de la liquidación (saldo: " + saldo.toPlainString() + ").";
        }
        return null;
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /** Los errores se devuelven como ResponseDTO, así que hay que marcar el rollback a mano. */
    private void marcarRollback() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException ignored) {
            // sin transacción activa no hay nada que revertir
        }
    }
}

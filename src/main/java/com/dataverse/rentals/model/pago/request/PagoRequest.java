package com.dataverse.rentals.model.pago.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PagoRequest {
    private Integer idPago;
    private Integer idLiquidacion;
    private LocalDate fechaPago;
    private BigDecimal montoPagado;
    private String metodoPago;
    private String numeroVoucher;
    private String rutaEvidencia;
}

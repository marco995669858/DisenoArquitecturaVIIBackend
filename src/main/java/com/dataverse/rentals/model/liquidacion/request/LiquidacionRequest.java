package com.dataverse.rentals.model.liquidacion.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class LiquidacionRequest {
    private Integer idLiquidacion;
    private Integer idContrato;
    private String periodo;
    private BigDecimal montoAlquiler;
    private BigDecimal montoLuz;
    private BigDecimal montoAgua;
    private BigDecimal montoOtros;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;
}

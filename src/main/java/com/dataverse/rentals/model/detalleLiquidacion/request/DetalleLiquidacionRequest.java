package com.dataverse.rentals.model.detalleLiquidacion.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetalleLiquidacionRequest {
    private Integer idDetalle;
    private Integer idLiquidacion;
    private String concepto;
    private String tipoConcepto;
    private BigDecimal monto;
    private Integer idProrrateo;
    private Integer idMantenimiento;
}

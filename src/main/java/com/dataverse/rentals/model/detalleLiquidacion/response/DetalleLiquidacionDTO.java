package com.dataverse.rentals.model.detalleLiquidacion.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleLiquidacionDTO {
    private Integer idDetalle;
    private Integer idLiquidacion;
    private String concepto;
    private String tipoConcepto;
    private BigDecimal monto;
    private Integer idProrrateo;
    private Integer idMantenimiento;
}

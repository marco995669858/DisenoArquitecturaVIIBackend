package com.dataverse.rentals.model.liquidacion.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidacionDTO {
    private Integer idLiquidacion;
    private Integer idContrato;
    private String numeroUnidad;
    private String nombreInquilino;
    private String periodo;
    private BigDecimal montoAlquiler;
    private BigDecimal montoLuz;
    private BigDecimal montoAgua;
    private BigDecimal montoOtros;
    private BigDecimal montoTotal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;
}

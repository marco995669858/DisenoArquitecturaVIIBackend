package com.dataverse.rentals.model.liquidacion.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Una liquidacion recien emitida por CU-06, con el desglose que la compone. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidacionGeneradaDTO {
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
}

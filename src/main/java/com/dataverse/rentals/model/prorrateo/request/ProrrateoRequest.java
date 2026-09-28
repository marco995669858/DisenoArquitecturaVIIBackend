package com.dataverse.rentals.model.prorrateo.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProrrateoRequest {
    private Integer idProrrateo;
    private Integer idUnidad;
    private Integer idRecibo;
    private String periodo;
    private String tipoServicio;
    private BigDecimal consumoIndividual;
    private BigDecimal consumoComunAsignado;
    private BigDecimal costoUnitario;
    private BigDecimal montoCalculado;
}

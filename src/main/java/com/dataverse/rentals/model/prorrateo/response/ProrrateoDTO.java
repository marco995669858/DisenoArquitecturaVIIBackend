package com.dataverse.rentals.model.prorrateo.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProrrateoDTO {
    private Integer idProrrateo;
    private Integer idUnidad;
    private String numeroUnidad;
    private Integer idRecibo;
    private String periodo;
    private String tipoServicio;
    private BigDecimal consumoIndividual;
    private BigDecimal consumoComunAsignado;
    private BigDecimal costoUnitario;
    private BigDecimal montoCalculado;
}

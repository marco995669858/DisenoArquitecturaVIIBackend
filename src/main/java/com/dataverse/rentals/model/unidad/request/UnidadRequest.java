package com.dataverse.rentals.model.unidad.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UnidadRequest {
    private Integer idUnidad;
    private Integer piso;
    private String numeroUnidad;
    private BigDecimal areaM2;
    private String caracteristicas;
    private String estado;
}

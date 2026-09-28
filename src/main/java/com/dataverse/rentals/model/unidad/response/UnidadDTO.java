package com.dataverse.rentals.model.unidad.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadDTO {
    private Integer idUnidad;
    private Integer piso;
    private String numeroUnidad;
    private BigDecimal areaM2;
    private String caracteristicas;
    private String estado;
}

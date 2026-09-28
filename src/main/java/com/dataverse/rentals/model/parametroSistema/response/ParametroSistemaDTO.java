package com.dataverse.rentals.model.parametroSistema.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParametroSistemaDTO {
    private Integer idParametro;
    private String clave;
    private String valor;
    private String descripcion;
}

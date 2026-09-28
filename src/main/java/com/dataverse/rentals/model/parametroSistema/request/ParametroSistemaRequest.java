package com.dataverse.rentals.model.parametroSistema.request;

import lombok.Data;

@Data
public class ParametroSistemaRequest {
    private Integer idParametro;
    private String clave;
    private String valor;
    private String descripcion;
}

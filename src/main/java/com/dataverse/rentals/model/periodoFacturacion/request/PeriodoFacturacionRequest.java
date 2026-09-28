package com.dataverse.rentals.model.periodoFacturacion.request;

import lombok.Data;

@Data
public class PeriodoFacturacionRequest {
    private Integer idPeriodo;
    private String periodo;
    private String estado;
}

package com.dataverse.rentals.model.medidor.request;

import lombok.Data;

@Data
public class MedidorRequest {
    private Integer idMedidor;
    private Integer idUnidad;
    private String tipoServicio;
    private String tipoMedidor;
    private String numeroSerie;
}

package com.dataverse.rentals.model.medidor.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedidorDTO {
    private Integer idMedidor;
    private Integer idUnidad;
    private String numeroUnidad;
    private String tipoServicio;
    private String tipoMedidor;
    private String numeroSerie;
}

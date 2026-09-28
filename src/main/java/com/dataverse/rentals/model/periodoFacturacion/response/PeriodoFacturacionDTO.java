package com.dataverse.rentals.model.periodoFacturacion.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoFacturacionDTO {
    private Integer idPeriodo;
    private String periodo;
    private String estado;
}

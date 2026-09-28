package com.dataverse.rentals.model.prorrateo.request;

import lombok.Data;

/** Entrada de CU-05 -- Calcular Prorrateo: periodo y servicio a calcular. */
@Data
public class CalcularProrrateoRequest {
    private String periodo;
    private String tipoServicio;
}

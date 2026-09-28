package com.dataverse.rentals.model.liquidacion.request;

import lombok.Data;

/** Entrada de CU-06 -- Generar Liquidacion: el periodo a liquidar (todas las unidades). */
@Data
public class GenerarLiquidacionRequest {
    private String periodo;
}

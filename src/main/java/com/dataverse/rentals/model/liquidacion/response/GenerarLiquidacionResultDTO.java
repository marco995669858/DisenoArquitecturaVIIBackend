package com.dataverse.rentals.model.liquidacion.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de CU-06 -- Generar Liquidacion para un periodo completo.
 * Separa lo emitido de lo que quedo afuera y por que, tal como pide el
 * flujo principal (paso 5, "liquidacion preliminar") y los flujos
 * alternos FA-01 y FA-02.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerarLiquidacionResultDTO {
    private List<LiquidacionGeneradaDTO> generadas;
    private List<String> excepciones;   // FA-01: unidad sin contrato vigente
    private List<String> omitidas;      // FA-02: ya existia liquidacion (RN-04)
    private List<String> advertencias;  // p. ej. no se pudo calcular el prorrateo de un servicio
}

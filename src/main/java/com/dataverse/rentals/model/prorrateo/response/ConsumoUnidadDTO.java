package com.dataverse.rentals.model.prorrateo.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lectura de trabajo interna para el calculo de CU-05: el consumo individual
 * del periodo de una unidad, mas los datos que hacen falta para repartir el
 * excedente (numero de unidad para mostrarlo, area para el criterio AREA de
 * S-04). No se persiste: es solo para calcular.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoUnidadDTO {
    private Integer idUnidad;
    private String numeroUnidad;
    private BigDecimal areaM2;
    private BigDecimal consumoIndividual;
}

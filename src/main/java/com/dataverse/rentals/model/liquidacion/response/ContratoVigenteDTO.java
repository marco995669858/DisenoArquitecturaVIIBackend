package com.dataverse.rentals.model.liquidacion.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lectura de trabajo interna de CU-06: un contrato vigente y los datos de su unidad/inquilino. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoVigenteDTO {
    private Integer idContrato;
    private Integer idUnidad;
    private String numeroUnidad;
    private Integer idInquilino;
    private String nombreInquilino;
    private BigDecimal montoAlquiler;
}

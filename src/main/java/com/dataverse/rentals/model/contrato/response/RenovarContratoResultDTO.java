package com.dataverse.rentals.model.contrato.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de CU-03b -- Renovar Contrato: el contrato anterior queda RENOVADO y este es el que lo continua. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenovarContratoResultDTO {
    private Integer idContratoAnterior;
    private Integer idContratoNuevo;
    private String numeroUnidad;
    private String nombreInquilino;
    private LocalDate nuevaFechaFin;
}

package com.dataverse.rentals.model.contrato.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Un contrato vigente cuya fecha_fin cae dentro de la ventana de alerta de RN-03. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoPorVencerDTO {
    private Integer idContrato;
    private String numeroUnidad;
    private String nombreInquilino;
    private LocalDate fechaFin;
    private Integer diasParaVencer;
}

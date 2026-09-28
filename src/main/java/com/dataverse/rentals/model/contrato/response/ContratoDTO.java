package com.dataverse.rentals.model.contrato.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoDTO {
    private Integer idContrato;
    private Integer idUnidad;
    private String numeroUnidad;
    private Integer idInquilino;
    private String nombreInquilino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal montoAlquiler;
    private String rutaContratoDigital;
    private String estado;
}

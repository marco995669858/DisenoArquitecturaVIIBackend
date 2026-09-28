package com.dataverse.rentals.model.contrato.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ContratoRequest {
    private Integer idContrato;
    private Integer idUnidad;
    private Integer idInquilino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal montoAlquiler;
    private String rutaContratoDigital;
    private String estado;
}

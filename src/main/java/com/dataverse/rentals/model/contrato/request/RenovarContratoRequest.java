package com.dataverse.rentals.model.contrato.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/** CU-03b -- Renovar Contrato. Los campos opcionales, si se omiten, se heredan del contrato que se renueva. */
@Data
public class RenovarContratoRequest {
    private Integer idContrato;
    private LocalDate nuevaFechaFin;
    private BigDecimal nuevoMontoAlquiler;
    private String nuevaRutaContratoDigital;
}

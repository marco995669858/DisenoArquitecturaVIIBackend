package com.dataverse.rentals.model.lecturaMedidor.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class LecturaMedidorRequest {
    private Integer idLectura;
    private Integer idMedidor;
    private String periodo;
    private BigDecimal lecturaAnterior;
    private BigDecimal lecturaActual;
    private LocalDate fechaLectura;
}

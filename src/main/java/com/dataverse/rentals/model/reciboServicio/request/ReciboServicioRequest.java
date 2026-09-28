package com.dataverse.rentals.model.reciboServicio.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReciboServicioRequest {
    private Integer idRecibo;
    private String periodo;
    private String tipoServicio;
    private BigDecimal montoTotal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String rutaDocumento;
}

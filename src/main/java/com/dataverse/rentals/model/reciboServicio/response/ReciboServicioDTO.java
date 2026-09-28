package com.dataverse.rentals.model.reciboServicio.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReciboServicioDTO {
    private Integer idRecibo;
    private String periodo;
    private String tipoServicio;
    private BigDecimal montoTotal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String rutaDocumento;
}

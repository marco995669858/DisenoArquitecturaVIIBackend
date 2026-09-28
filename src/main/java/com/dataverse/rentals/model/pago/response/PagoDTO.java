package com.dataverse.rentals.model.pago.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Integer idPago;
    private Integer idLiquidacion;
    private String numeroUnidad;
    private String periodo;
    private Integer idUsuario;
    private String nombreUsuario;
    private LocalDate fechaPago;
    private BigDecimal montoPagado;
    private String metodoPago;
    private String numeroVoucher;
    private String rutaEvidencia;
}

package com.dataverse.rentals.model.lecturaMedidor.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaMedidorDTO {
    private Integer idLectura;
    private Integer idMedidor;
    private String numeroSerie;
    private String tipoServicio;
    private String numeroUnidad;
    private Integer idUsuario;
    private String periodo;
    private BigDecimal lecturaAnterior;
    private BigDecimal lecturaActual;
    private BigDecimal consumo;
    private LocalDate fechaLectura;
}

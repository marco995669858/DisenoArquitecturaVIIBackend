package com.pe.consultorio.clinico.model.tipoConsullta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoConsultaDTO {
    private Integer idTipoConsulta;
    private String nombre;
    private Integer duracionEstimadaMinutos;
    private double precioBase;
}

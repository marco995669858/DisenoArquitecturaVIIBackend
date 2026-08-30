package com.pe.consultorio.clinico.model.tipoConsullta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoConsultaRequest {
    private Integer idTipoConsulta;
    private String nombre;
    private Integer duracionEstimadaMinutos;
    private Double precioBase;
    private String usuario;
}

package com.pe.consultorio.clinico.model.especialidad.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecialidadDTO {
    private Integer idEspecialidad;
    private String nombre;
    private String descripcion;
}

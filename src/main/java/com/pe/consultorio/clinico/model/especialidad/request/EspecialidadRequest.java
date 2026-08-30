package com.pe.consultorio.clinico.model.especialidad.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecialidadRequest {
    private Integer idEspecialidad;
    private String nombre;
    private String descripcion;
    private String usuario;
}

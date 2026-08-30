package com.pe.consultorio.clinico.service;

import java.util.List;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.especialidad.request.EspecialidadRequest;
import com.pe.consultorio.clinico.model.especialidad.response.EspecialidadDTO;

public interface ServiceEspecialidad {

   ResponseDTO<List<EspecialidadDTO>> listarEspecialidades();

   ResponseDTO<Integer> insertUpdateEspecialidad(EspecialidadRequest args);

   ResponseDTO<Integer> eliminarEspecialidad(EspecialidadRequest args);
}

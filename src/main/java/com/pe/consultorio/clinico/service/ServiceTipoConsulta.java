package com.pe.consultorio.clinico.service;

import java.util.List;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.tipoConsullta.request.TipoConsultaRequest;
import com.pe.consultorio.clinico.model.tipoConsullta.response.TipoConsultaDTO;

public interface ServiceTipoConsulta {

    ResponseDTO<List<TipoConsultaDTO>> listarTipoConsultas();

    ResponseDTO<Integer> insertUpdateTipoConsulta(TipoConsultaRequest args);

    ResponseDTO<Integer> eliminarTipoConsulta(TipoConsultaRequest args);
}

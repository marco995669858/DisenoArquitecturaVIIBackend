package com.pe.consultorio.clinico.service;

import java.util.List;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.estadoCita.request.EstadoCitaRequest;
import com.pe.consultorio.clinico.model.estadoCita.response.EstadoCitaDTO;

public interface ServiceEstadoCita {

    ResponseDTO<List<EstadoCitaDTO>> listarEstadoCitas();

    ResponseDTO<Integer> insertUpdateEstadoCita(EstadoCitaRequest args);

    ResponseDTO<Integer> eliminarEstadoCita(EstadoCitaRequest args);
}

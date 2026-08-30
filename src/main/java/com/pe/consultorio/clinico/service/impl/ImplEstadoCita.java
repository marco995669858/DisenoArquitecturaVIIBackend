package com.pe.consultorio.clinico.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperEstadoCita;
import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.estadoCita.request.EstadoCitaRequest;
import com.pe.consultorio.clinico.model.estadoCita.response.EstadoCitaDTO;
import com.pe.consultorio.clinico.service.ServiceEstadoCita;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplEstadoCita implements ServiceEstadoCita {

    private final MapperEstadoCita mapper;

    @Override
    public ResponseDTO<List<EstadoCitaDTO>> listarEstadoCitas() {
        ResponseDTO<List<EstadoCitaDTO>> response;
        ResultCustomPage<EstadoCitaDTO> resultPage = new ResultCustomPage<>();
        List<EstadoCitaDTO> data;
        try {
            mapper.listarEstadoCitas(resultPage);
            data = resultPage.getData();
            if (data.size() <= 0) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            response = new ResponseDTO<>(data, false, "Se encontraron registros.");

        } catch (Exception e) {
            log.error(e.getMessage());
            response = new ResponseDTO<>(true, e.getMessage());
        }

        return response;
    }

    @Override
    public ResponseDTO<Integer> insertUpdateEstadoCita(EstadoCitaRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<EstadoCitaRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.insertUpdateEstadoCita(request);
            if (request.getEstadoRespuesta() <= 0) {
                return new ResponseDTO<>(true, request.getDetalleRespuesta());
            }

            response = new ResponseDTO<>(request.getEstadoRespuesta(), false, request.getDetalleRespuesta());
        } catch (Exception e) {
            log.error(e.getMessage());
            response = new ResponseDTO<>(true, e.getMessage());
        }

        return response;
    }

    @Override
    public ResponseDTO<Integer> eliminarEstadoCita(EstadoCitaRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<EstadoCitaRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.eliminarEstadoCita(request);
            if (request.getEstadoRespuesta() <= 0) {
                return new ResponseDTO<>(true, request.getDetalleRespuesta());
            }

            response = new ResponseDTO<>(request.getEstadoRespuesta(), false, request.getDetalleRespuesta());
        } catch (Exception e) {
            log.error(e.getMessage());
            response = new ResponseDTO<>(true, e.getMessage());
        }

        return response;
    }

}

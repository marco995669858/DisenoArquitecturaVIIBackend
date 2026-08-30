package com.pe.consultorio.clinico.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperEspecialidad;
import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.especialidad.request.EspecialidadRequest;
import com.pe.consultorio.clinico.model.especialidad.response.EspecialidadDTO;
import com.pe.consultorio.clinico.service.ServiceEspecialidad;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplEspecialidad implements ServiceEspecialidad {

    private final MapperEspecialidad mapper;

    @Override
    public ResponseDTO<List<EspecialidadDTO>> listarEspecialidades() {
        ResponseDTO<List<EspecialidadDTO>> response;
        ResultCustomPage<EspecialidadDTO> resultPage = new ResultCustomPage<>();
        List<EspecialidadDTO> data;
        try {
            mapper.listarEspecialidades(resultPage);
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
    public ResponseDTO<Integer> insertUpdateEspecialidad(EspecialidadRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<EspecialidadRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.registrarActualizarEspecialidad(request);
            if (request.getEstadoRespuesta() <= 0) {
                return new ResponseDTO<>(true, request.getDetalleRespuesta());
            }

            response = new ResponseDTO<>(request.getEstadoRespuesta(),false, request.getDetalleRespuesta());
        } catch (Exception e) {
            log.error(e.getMessage());
            response = new ResponseDTO<>(true, e.getMessage());
        }

        return response;
    }

    @Override
    public ResponseDTO<Integer> eliminarEspecialidad(EspecialidadRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<EspecialidadRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.eliminarEspecialidad(request);
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

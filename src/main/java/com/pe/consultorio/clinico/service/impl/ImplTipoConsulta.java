package com.pe.consultorio.clinico.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperTipoConsulta;
import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.tipoConsullta.request.TipoConsultaRequest;
import com.pe.consultorio.clinico.model.tipoConsullta.response.TipoConsultaDTO;
import com.pe.consultorio.clinico.service.ServiceTipoConsulta;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplTipoConsulta implements ServiceTipoConsulta {

    private final MapperTipoConsulta mapper;

    @Override
    public ResponseDTO<List<TipoConsultaDTO>> listarTipoConsultas() {
        ResponseDTO<List<TipoConsultaDTO>> response;
        ResultCustomPage<TipoConsultaDTO> resultPage = new ResultCustomPage<>();
        List<TipoConsultaDTO> data;
        try {
            mapper.listarTipoConsultas(resultPage);
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
    public ResponseDTO<Integer> insertUpdateTipoConsulta(TipoConsultaRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<TipoConsultaRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.insertUpdateTipoConsulta(request);
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
    public ResponseDTO<Integer> eliminarTipoConsulta(TipoConsultaRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<TipoConsultaRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.eliminarTipoConsulta(request);
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

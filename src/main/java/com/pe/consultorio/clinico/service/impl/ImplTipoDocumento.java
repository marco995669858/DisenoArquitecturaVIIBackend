package com.pe.consultorio.clinico.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperTipoDocumento;
import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.tipoDocumento.request.TipoDocumentoRequest;
import com.pe.consultorio.clinico.model.tipoDocumento.response.TipoDocumentoDTO;
import com.pe.consultorio.clinico.service.ServiceTipoDocumento;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplTipoDocumento implements ServiceTipoDocumento {

    private final MapperTipoDocumento mapper;

    @Override
    public ResponseDTO<List<TipoDocumentoDTO>> listarTipoDocumentos() {
        ResponseDTO<List<TipoDocumentoDTO>> response;
        ResultCustomPage<TipoDocumentoDTO> resultPage = new ResultCustomPage<>();
        List<TipoDocumentoDTO> data;
        try {
            mapper.listarTipoDocumentos(resultPage);
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
    public ResponseDTO<Integer> insertUpdateTipoDocumento(TipoDocumentoRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<TipoDocumentoRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.insertUpdateTipoDocumento(request);
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
    public ResponseDTO<Integer> eliminarTipoDocumento(TipoDocumentoRequest args) {
        ResponseDTO<Integer> response;
        ResultCustomRUD<TipoDocumentoRequest> request = new ResultCustomRUD<>();
        try {
            request.setData(args);
            mapper.eliminarTipoDocumento(request);
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

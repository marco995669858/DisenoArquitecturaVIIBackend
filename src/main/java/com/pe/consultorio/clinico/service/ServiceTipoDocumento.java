package com.pe.consultorio.clinico.service;

import java.util.List;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.tipoDocumento.request.TipoDocumentoRequest;
import com.pe.consultorio.clinico.model.tipoDocumento.response.TipoDocumentoDTO;

public interface ServiceTipoDocumento {

    ResponseDTO<List<TipoDocumentoDTO>> listarTipoDocumentos();

    ResponseDTO<Integer> insertUpdateTipoDocumento(TipoDocumentoRequest args);

    ResponseDTO<Integer> eliminarTipoDocumento(TipoDocumentoRequest args);
}

package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.parametroSistema.request.ParametroSistemaRequest;
import com.dataverse.rentals.model.parametroSistema.response.ParametroSistemaDTO;

public interface ServiceParametroSistema {

    ResponseDTO<List<ParametroSistemaDTO>> listarParametros();

    ResponseDTO<Integer> insertUpdateParametro(ParametroSistemaRequest args);

    ResponseDTO<Integer> eliminarParametro(ParametroSistemaRequest args);
}

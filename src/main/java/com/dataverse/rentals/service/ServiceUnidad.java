package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.unidad.request.UnidadRequest;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;

public interface ServiceUnidad {

    ResponseDTO<List<UnidadDTO>> listarUnidades();

    ResponseDTO<Integer> insertUpdateUnidad(UnidadRequest args);

    ResponseDTO<Integer> eliminarUnidad(UnidadRequest args);
}

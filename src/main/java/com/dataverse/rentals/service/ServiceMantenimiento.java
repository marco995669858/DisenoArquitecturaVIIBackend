package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.mantenimiento.request.MantenimientoRequest;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;

public interface ServiceMantenimiento {

    ResponseDTO<List<MantenimientoDTO>> listarMantenimientos();

    ResponseDTO<Integer> insertUpdateMantenimiento(MantenimientoRequest args);

    ResponseDTO<Integer> eliminarMantenimiento(MantenimientoRequest args);
}

package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.detalleLiquidacion.request.DetalleLiquidacionRequest;
import com.dataverse.rentals.model.detalleLiquidacion.response.DetalleLiquidacionDTO;

public interface ServiceDetalleLiquidacion {

    ResponseDTO<List<DetalleLiquidacionDTO>> listarPorLiquidacion(Integer idLiquidacion);

    ResponseDTO<Integer> insertUpdateDetalle(DetalleLiquidacionRequest args);

    ResponseDTO<Integer> eliminarDetalle(DetalleLiquidacionRequest args);
}

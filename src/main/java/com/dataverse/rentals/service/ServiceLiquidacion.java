package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.liquidacion.request.GenerarLiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.request.LiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.response.GenerarLiquidacionResultDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;

public interface ServiceLiquidacion {

    ResponseDTO<List<LiquidacionDTO>> listarLiquidaciones();

    ResponseDTO<Integer> insertUpdateLiquidacion(LiquidacionRequest args);

    ResponseDTO<Integer> eliminarLiquidacion(LiquidacionRequest args);

    /** CU-06 -- Generar Liquidacion: compone y emite la liquidacion de cada unidad para un periodo. */
    ResponseDTO<GenerarLiquidacionResultDTO> generarLiquidaciones(GenerarLiquidacionRequest args);

    /** Marca VENCIDO las liquidaciones PENDIENTE/PARCIAL cuya fecha_vencimiento ya paso. */
    ResponseDTO<Integer> verificarVencidas();
}

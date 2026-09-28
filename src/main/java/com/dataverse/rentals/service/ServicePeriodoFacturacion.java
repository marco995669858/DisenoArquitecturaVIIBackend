package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.periodoFacturacion.request.PeriodoFacturacionRequest;
import com.dataverse.rentals.model.periodoFacturacion.response.PeriodoFacturacionDTO;

public interface ServicePeriodoFacturacion {

    ResponseDTO<List<PeriodoFacturacionDTO>> listarPeriodos();

    ResponseDTO<Integer> insertUpdatePeriodo(PeriodoFacturacionRequest args);
}

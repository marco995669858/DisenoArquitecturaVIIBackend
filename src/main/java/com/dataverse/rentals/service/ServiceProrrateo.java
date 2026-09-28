package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.prorrateo.request.CalcularProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.request.ProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.response.ProrrateoDTO;

public interface ServiceProrrateo {

    ResponseDTO<List<ProrrateoDTO>> listarProrrateos();

    ResponseDTO<Integer> insertUpdateProrrateo(ProrrateoRequest args);

    ResponseDTO<Integer> eliminarProrrateo(ProrrateoRequest args);

    /** CU-05 -- Calcular Prorrateo: deriva el prorrateo de un periodo/servicio a partir de las lecturas ya registradas. */
    ResponseDTO<List<ProrrateoDTO>> calcularProrrateo(CalcularProrrateoRequest args);
}

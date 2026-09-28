package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.pago.request.PagoRequest;
import com.dataverse.rentals.model.pago.response.PagoDTO;

public interface ServicePago {

    ResponseDTO<List<PagoDTO>> listarPagos();

    ResponseDTO<Integer> insertUpdatePago(PagoRequest args);

    ResponseDTO<Integer> eliminarPago(PagoRequest args);
}

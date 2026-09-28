package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.reciboServicio.request.ReciboServicioRequest;
import com.dataverse.rentals.model.reciboServicio.response.ReciboServicioDTO;

public interface ServiceReciboServicio {

    ResponseDTO<List<ReciboServicioDTO>> listarRecibos();

    ResponseDTO<Integer> insertUpdateRecibo(ReciboServicioRequest args);

    ResponseDTO<Integer> eliminarRecibo(ReciboServicioRequest args);
}

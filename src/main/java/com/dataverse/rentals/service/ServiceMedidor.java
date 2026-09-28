package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.medidor.request.MedidorRequest;
import com.dataverse.rentals.model.medidor.response.MedidorDTO;

public interface ServiceMedidor {

    ResponseDTO<List<MedidorDTO>> listarMedidores();

    ResponseDTO<Integer> insertUpdateMedidor(MedidorRequest args);

    ResponseDTO<Integer> eliminarMedidor(MedidorRequest args);
}

package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.lecturaMedidor.request.LecturaMedidorRequest;
import com.dataverse.rentals.model.lecturaMedidor.response.LecturaMedidorDTO;

public interface ServiceLecturaMedidor {

    ResponseDTO<List<LecturaMedidorDTO>> listarLecturas();

    ResponseDTO<Integer> insertUpdateLectura(LecturaMedidorRequest args);

    ResponseDTO<Integer> eliminarLectura(LecturaMedidorRequest args);
}

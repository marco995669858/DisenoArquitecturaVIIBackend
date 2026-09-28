package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.inquilino.request.InquilinoRequest;
import com.dataverse.rentals.model.inquilino.response.InquilinoDTO;

public interface ServiceInquilino {

    ResponseDTO<List<InquilinoDTO>> listarInquilinos();

    ResponseDTO<Integer> insertUpdateInquilino(InquilinoRequest args);

    ResponseDTO<Integer> eliminarInquilino(InquilinoRequest args);
}

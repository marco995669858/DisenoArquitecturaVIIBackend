package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperModuloSistema;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.ModuloSistemaDTO;
import com.dataverse.rentals.service.ServiceModuloSistema;
import com.dataverse.rentals.util.ErrorMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplModuloSistema implements ServiceModuloSistema {

    private final MapperModuloSistema mapper;

    @Override
    public ResponseDTO<List<ModuloSistemaDTO>> listarModulos() {
        try {
            return new ResponseDTO<>(mapper.listarModulos(), false, "OK");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

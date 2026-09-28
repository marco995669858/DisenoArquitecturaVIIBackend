package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperAuditoria;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.auditoria.response.EventoAuditoriaDTO;
import com.dataverse.rentals.service.ServiceAuditoria;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImplAuditoria implements ServiceAuditoria {

    private static final int LIMITE_MAXIMO = 500;

    private final MapperAuditoria mapper;

    @Override
    public ResponseDTO<List<EventoAuditoriaDTO>> listarHistorial(int limite) {
        int limiteSeguro = limite <= 0 || limite > LIMITE_MAXIMO ? 200 : limite;
        List<EventoAuditoriaDTO> eventos = mapper.listarEventos(limiteSeguro);
        return new ResponseDTO<>(eventos, false, "OK");
    }
}

package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.parametroSistema.request.ParametroSistemaRequest;
import com.dataverse.rentals.model.parametroSistema.response.ParametroSistemaDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceParametroSistema;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplParametroSistema implements ServiceParametroSistema {

    private final MapperParametroSistema mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<ParametroSistemaDTO>> listarParametros() {
        try {
            List<ParametroSistemaDTO> data = mapper.listarParametros();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> insertUpdateParametro(ParametroSistemaRequest args) {
        try {
            if (args.getClave() == null || args.getClave().isBlank()) {
                return new ResponseDTO<>(true, "Debe indicar la clave del parámetro.");
            }
            if (args.getIdParametro() == null) {
                mapper.insertarParametro(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdParametro(), false, "Parámetro registrado correctamente.");
            }
            mapper.actualizarParametro(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdParametro(), false, "Parámetro actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarParametro(ParametroSistemaRequest args) {
        try {
            int filas = mapper.eliminarParametro(args.getIdParametro(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el parámetro a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Parámetro eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

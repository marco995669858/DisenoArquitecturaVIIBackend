package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperInquilino;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.inquilino.request.InquilinoRequest;
import com.dataverse.rentals.model.inquilino.response.InquilinoDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceInquilino;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplInquilino implements ServiceInquilino {

    private final MapperInquilino mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<InquilinoDTO>> listarInquilinos() {
        try {
            List<InquilinoDTO> data = mapper.listarInquilinos();
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
    public ResponseDTO<Integer> insertUpdateInquilino(InquilinoRequest args) {
        try {
            if (args.getIdInquilino() == null) {
                mapper.insertarInquilino(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdInquilino(), false, "Inquilino registrado correctamente.");
            }
            mapper.actualizarInquilino(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdInquilino(), false, "Inquilino actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarInquilino(InquilinoRequest args) {
        try {
            if (mapper.contarContratosDeInquilino(args.getIdInquilino()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar el inquilino: tiene un contrato asociado.");
            }

            int filas = mapper.eliminarInquilino(args.getIdInquilino(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el inquilino a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Inquilino eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

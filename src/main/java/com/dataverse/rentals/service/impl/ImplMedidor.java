package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperMedidor;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.medidor.request.MedidorRequest;
import com.dataverse.rentals.model.medidor.response.MedidorDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceMedidor;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplMedidor implements ServiceMedidor {

    private final MapperMedidor mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<MedidorDTO>> listarMedidores() {
        try {
            List<MedidorDTO> data = mapper.listarMedidores();
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
    public ResponseDTO<Integer> insertUpdateMedidor(MedidorRequest args) {
        try {
            if (args.getIdMedidor() == null) {
                mapper.insertarMedidor(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdMedidor(), false, "Medidor registrado correctamente.");
            }
            mapper.actualizarMedidor(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdMedidor(), false, "Medidor actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarMedidor(MedidorRequest args) {
        try {
            if (mapper.contarLecturasDeMedidor(args.getIdMedidor()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar el medidor: tiene lecturas registradas.");
            }

            int filas = mapper.eliminarMedidor(args.getIdMedidor(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el medidor a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Medidor eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

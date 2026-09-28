package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperUnidad;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.unidad.request.UnidadRequest;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceUnidad;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplUnidad implements ServiceUnidad {

    // RNE (Reglamento Nacional de Edificaciones): area techada minima para un
    // departamento de un dormitorio (vivienda sin capacidad de ampliacion).
    private static final BigDecimal AREA_MINIMA_M2 = new BigDecimal("40");

    private final MapperUnidad mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<UnidadDTO>> listarUnidades() {
        try {
            List<UnidadDTO> data = mapper.listarUnidades();
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
    public ResponseDTO<Integer> insertUpdateUnidad(UnidadRequest args) {
        try {
            if (args.getAreaM2() == null || args.getAreaM2().compareTo(AREA_MINIMA_M2) < 0) {
                return new ResponseDTO<>(true, "El área debe ser de al menos 40 m² (área techada mínima para un "
                        + "departamento de un dormitorio, según el Reglamento Nacional de Edificaciones).");
            }

            if (args.getIdUnidad() == null) {
                mapper.insertarUnidad(args, audit.usuarioActual(), audit.ipActual());
                return new ResponseDTO<>(args.getIdUnidad(), false, "Unidad registrada correctamente.");
            }
            mapper.actualizarUnidad(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdUnidad(), false, "Unidad actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarUnidad(UnidadRequest args) {
        try {
            Integer idUnidad = args.getIdUnidad();
            if (mapper.contarContratosDeUnidad(idUnidad) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la unidad: tiene un contrato asociado.");
            }
            if (mapper.contarMedidoresDeUnidad(idUnidad) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la unidad: tiene medidores asociados.");
            }
            if (mapper.contarProrrateosDeUnidad(idUnidad) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la unidad: tiene prorrateos asociados.");
            }
            if (mapper.contarMantenimientosDeUnidad(idUnidad) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la unidad: tiene mantenimientos asociados.");
            }

            int filas = mapper.eliminarUnidad(idUnidad, audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró la unidad a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Unidad eliminada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

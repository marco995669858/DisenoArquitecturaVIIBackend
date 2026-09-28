package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.auditoria.response.EventoAuditoriaDTO;

public interface ServiceAuditoria {

    /** RNF-06: historial de auditoria (creacion/actualizacion/eliminacion) de las entidades de negocio. */
    ResponseDTO<List<EventoAuditoriaDTO>> listarHistorial(int limite);
}

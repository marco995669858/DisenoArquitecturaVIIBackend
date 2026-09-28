package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.ModuloSistemaDTO;

public interface ServiceModuloSistema {

    /** Catalogo completo de modulos asignables como permiso a un rol (ver pantalla Roles). */
    ResponseDTO<List<ModuloSistemaDTO>> listarModulos();
}

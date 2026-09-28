package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.rol.request.RolRequest;
import com.dataverse.rentals.model.rol.response.RolDTO;

public interface ServiceRol {

    ResponseDTO<List<RolDTO>> listarRoles();

    ResponseDTO<Integer> insertUpdateRol(RolRequest args);

    ResponseDTO<Integer> eliminarRol(RolRequest args);

    /** Permisos (modulo + nivel de acceso) que tiene asignados un rol, para precargar el formulario al editar. */
    ResponseDTO<List<PermisoAsignadoDTO>> listarPermisosDeRol(Integer idRol);
}

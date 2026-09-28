package com.dataverse.rentals.model.rol.request;

import java.util.List;

import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;

import lombok.Data;

@Data
public class RolRequest {
    private Integer idRol;
    private String nombre;
    /** Modulos que este rol puede ver, cada uno con su nivel de acceso (ver ImplRol.insertUpdateRol). */
    private List<PermisoAsignadoDTO> permisos;
}

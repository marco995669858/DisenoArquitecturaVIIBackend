package com.dataverse.rentals.model.modulo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un permiso concreto: modulo + nivel de acceso (LECTURA o ESCRITURA). Se usa
 * tanto para guardar los permisos de un rol (RolRequest.permisos) como para
 * consultarlos (ControllerRol.listarPermisosDeRol, ControllerUsuario.mis-permisos).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermisoAsignadoDTO {
    private String codigo;
    private String nivelAcceso;
}

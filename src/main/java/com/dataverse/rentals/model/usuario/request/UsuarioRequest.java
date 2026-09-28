package com.dataverse.rentals.model.usuario.request;

import lombok.Data;

/**
 * Alta/edicion de un usuario del sistema. nombreUsuario no se puede editar
 * (es lo que identifica al usuario para el login); password solo se usa al
 * crear -- para cambiar la clave de un usuario existente se usa
 * CambiarPasswordRequest, no este mismo objeto.
 */
@Data
public class UsuarioRequest {
    private Integer idUsuario;
    private Integer idRol;
    private String nombreUsuario;
    private String password;
    private String nombreCompleto;
    private String email;
    private String estado;
}

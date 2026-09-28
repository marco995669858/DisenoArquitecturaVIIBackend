package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Nunca incluye password_hash: este DTO es el que viaja al cliente. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private Integer idRol;
    private String nombreRol;
    private String nombreUsuario;
    private String nombreCompleto;
    private String email;
    private String estado;
}

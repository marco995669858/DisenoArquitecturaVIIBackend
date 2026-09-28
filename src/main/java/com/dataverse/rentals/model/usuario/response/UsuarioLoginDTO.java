package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginDTO {
    private Integer idUsuario;
    private String username;
    private String password;
    private Boolean activo;
    private String roles;
}

package com.dataverse.rentals.model.usuario.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    private Integer idTokenRefresco;
    private Integer idUsuario;
    private String username;
    private String token;
    private Instant fechaExpiracion;
}

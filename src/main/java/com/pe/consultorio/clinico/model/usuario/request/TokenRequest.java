package com.pe.consultorio.clinico.model.usuario.request;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private Integer idUsuario;
    private Integer idEmpresa;
    private String username;
    private Instant fechaExpiracion;
    private String usuario;
}

package com.pe.consultorio.clinico.model.usuario.response;

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
    private Integer idEmpresa;
    private String tipoNegocio;
    private String roles;
}

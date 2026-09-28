package com.dataverse.rentals.model.usuario.request;

import lombok.Data;

@Data
public class CambiarPasswordRequest {
    private Integer idUsuario;
    private String passwordNueva;
}

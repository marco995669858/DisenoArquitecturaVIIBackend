package com.dataverse.rentals.model.usuario.request;

import lombok.Data;

/** Segundo paso del login cuando el usuario tiene MFA activo: el token temporal que entrego /auth/login, mas el codigo de 6 digitos. */
@Data
public class VerificarMfaRequest {
    private String mfaToken;
    private String codigo;
}

package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de /auth/login. Segun el estado de MFA del usuario, trae una de
 * tres formas: login completo (token/refreshToken), verificacion pendiente
 * (mfaRequerido + mfaToken) o activacion pendiente (mfaSetupRequerido +
 * mfaToken + los datos para armar el QR).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String token;
    private String refreshToken;
    private boolean mfaRequerido;
    private boolean mfaSetupRequerido;
    private String mfaToken;
    private String mfaSecretoManual;
    private String mfaOtpAuthUri;
}

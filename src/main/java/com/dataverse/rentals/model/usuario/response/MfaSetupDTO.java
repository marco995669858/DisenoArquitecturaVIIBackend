package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos para activar la verificacion en dos pasos: el secreto (entrada manual) y la URI para el codigo QR. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MfaSetupDTO {
    private String secretoManual;
    private String otpAuthUri;
}

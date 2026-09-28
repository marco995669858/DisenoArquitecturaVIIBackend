package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Estado de MFA seguro para exponer al frontend (nunca incluye el secreto). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoMfaDTO {
    private boolean mfaHabilitado;
    private boolean mfaConfirmado;
}

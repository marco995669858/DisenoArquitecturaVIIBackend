package com.dataverse.rentals.model.usuario.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Uso interno de ImplUsuario unicamente -- incluye el secreto, nunca se devuelve tal cual a un controller. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoMfaInternoDTO {
    private Boolean mfaHabilitado;
    private String mfaSecret;
    private Boolean mfaConfirmado;
}

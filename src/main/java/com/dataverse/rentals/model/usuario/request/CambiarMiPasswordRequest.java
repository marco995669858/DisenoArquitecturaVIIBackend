package com.dataverse.rentals.model.usuario.request;

import lombok.Data;

/**
 * Cambio de contrasena que hace un usuario sobre su PROPIA cuenta (a
 * diferencia de CambiarPasswordRequest, que usa el administrador para
 * resetear la de otro usuario sin conocer la actual). idUsuario nunca viaja
 * aca: sale de AuditContext, para que nadie pueda cambiar la contrasena de
 * otra persona por esta via.
 */
@Data
public class CambiarMiPasswordRequest {
    private String passwordActual;
    private String passwordNueva;
}

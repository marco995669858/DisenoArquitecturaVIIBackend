package com.dataverse.rentals.model.usuario.request;

import lombok.Data;

/** Codigo de 6 digitos para confirmar o desactivar la propia verificacion en dos pasos (el usuario sale de AuditContext, no de aca). */
@Data
public class CodigoMfaRequest {
    private String codigo;
}

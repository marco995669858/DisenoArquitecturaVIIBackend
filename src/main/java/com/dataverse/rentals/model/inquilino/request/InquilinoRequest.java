package com.dataverse.rentals.model.inquilino.request;

import lombok.Data;

@Data
public class InquilinoRequest {
    private Integer idInquilino;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombreCompleto;
    private String telefono;
    private String email;
    private String direccion;
}

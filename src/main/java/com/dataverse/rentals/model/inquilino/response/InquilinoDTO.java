package com.dataverse.rentals.model.inquilino.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquilinoDTO {
    private Integer idInquilino;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombreCompleto;
    private String telefono;
    private String email;
    private String direccion;
}

package com.pe.consultorio.clinico.model.tipoDocumento.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumentoRequest {
    private Integer idTipoDocumento;
    private String nombre;
    private String abreviatura;
    private String usuario;
}

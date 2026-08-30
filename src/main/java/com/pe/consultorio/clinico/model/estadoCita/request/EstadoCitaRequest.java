package com.pe.consultorio.clinico.model.estadoCita.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoCitaRequest {
    private Integer idEstadoCita;
    private String nombre;
    private String colorHex;
    private String usuario;
}

package com.pe.consultorio.clinico.model.estadoCita.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoCitaDTO {
    private Integer idEstadoCita;
    private String nombre;
    private String colorHex;
}

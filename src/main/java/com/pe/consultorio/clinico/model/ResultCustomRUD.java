package com.pe.consultorio.clinico.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultCustomRUD<T> {
    private T data;
    private Integer estadoRespuesta;
    private String detalleRespuesta;
}

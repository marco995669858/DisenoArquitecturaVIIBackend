package com.pe.consultorio.clinico.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T data;
    private Integer paginacion; //Para cuando exista una paginación.
    private boolean error; /* Codigo Error */
    private String mensaje; /* Mensaje Error */
    
    public ResponseDTO() {

    }
    
    public ResponseDTO(T data, boolean error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
        this.data = data;
    }
    
    public ResponseDTO(boolean error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }

    public ResponseDTO(T data, Integer paginacion , boolean error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
        this.data = data;
        this.paginacion = paginacion;
    }
    
}

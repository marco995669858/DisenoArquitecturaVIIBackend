package com.dataverse.rentals.model.auditoria.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Una fila del historial de auditoria: un evento de creacion/actualizacion/eliminacion sobre una entidad de negocio. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoAuditoriaDTO {
    private LocalDateTime fecha;
    private String accion; // CREACION | ACTUALIZACION | ELIMINACION
    private String entidad; // Unidad, Contrato, Liquidacion, etc.
    private String descripcion;
    private String nombreUsuario;
    private String ip;
}

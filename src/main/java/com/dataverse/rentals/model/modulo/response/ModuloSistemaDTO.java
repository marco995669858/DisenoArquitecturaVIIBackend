package com.dataverse.rentals.model.modulo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Un modulo (pantalla) del catalogo que se puede asignar como permiso a un rol. Ver modulo_sistema. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuloSistemaDTO {
    private Integer idModulo;
    private String codigo;
    private String nombre;
}

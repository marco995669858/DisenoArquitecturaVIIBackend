package com.dataverse.rentals.model.liquidacion.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Un gasto de mantenimiento imputable al inquilino, pendiente de incluir en una liquidacion. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoImputableDTO {
    private Integer idMantenimiento;
    private String descripcion;
    private BigDecimal monto;
}

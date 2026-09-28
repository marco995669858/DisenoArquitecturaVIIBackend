package com.dataverse.rentals.model.mantenimiento.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoDTO {
    private Integer idMantenimiento;
    private Integer idUnidad;
    private String numeroUnidad;
    private String ubicacion;
    private Integer idUsuario;
    private String nombreUsuario;
    private String descripcion;
    private LocalDate fechaMantenimiento;
    private String tipoGasto;
    private String proveedor;
    private BigDecimal montoMateriales;
    private BigDecimal montoManoObra;
    private BigDecimal monto;
    private Boolean imputableInquilino;
}

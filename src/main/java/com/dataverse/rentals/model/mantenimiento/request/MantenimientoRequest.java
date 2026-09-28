package com.dataverse.rentals.model.mantenimiento.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class MantenimientoRequest {
    private Integer idMantenimiento;
    private Integer idUnidad;
    private String ubicacion;
    private String descripcion;
    private LocalDate fechaMantenimiento;
    private String tipoGasto;
    private String proveedor;
    private BigDecimal montoMateriales;
    private BigDecimal montoManoObra;
    private BigDecimal monto;
    private Boolean imputableInquilino;
}

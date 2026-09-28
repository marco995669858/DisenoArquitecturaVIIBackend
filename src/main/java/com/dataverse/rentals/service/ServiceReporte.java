package com.dataverse.rentals.service;

public interface ServiceReporte {

    /** Excel de liquidaciones; periodo null/blank exporta todos los periodos. */
    byte[] generarLiquidacionesExcel(String periodo);

    /** PDF de liquidaciones; periodo null/blank exporta todos los periodos. */
    byte[] generarLiquidacionesPdf(String periodo);

    /** PDF del dashboard de rentabilidad (RF-09) para un periodo puntual. */
    byte[] generarDashboardPdf(String periodo);
}

package com.dataverse.rentals.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.service.ServiceReporte;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}reporte")
@RequiredArgsConstructor
public class ControllerReporte {

    private static final MediaType EXCEL = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ServiceReporte service;

    @GetMapping(value = "/liquidaciones/excel")
    public ResponseEntity<byte[]> liquidacionesExcel(@RequestParam(required = false) String periodo) {
        byte[] archivo = service.generarLiquidacionesExcel(periodo);
        String nombreArchivo = "liquidaciones" + (StringUtils.hasText(periodo) ? "-" + periodo : "") + ".xlsx";
        return archivoDescargable(archivo, EXCEL, nombreArchivo);
    }

    @GetMapping(value = "/liquidaciones/pdf")
    public ResponseEntity<byte[]> liquidacionesPdf(@RequestParam(required = false) String periodo) {
        byte[] archivo = service.generarLiquidacionesPdf(periodo);
        String nombreArchivo = "liquidaciones" + (StringUtils.hasText(periodo) ? "-" + periodo : "") + ".pdf";
        return archivoDescargable(archivo, MediaType.APPLICATION_PDF, nombreArchivo);
    }

    @GetMapping(value = "/dashboard/pdf")
    public ResponseEntity<byte[]> dashboardPdf(@RequestParam String periodo) {
        byte[] archivo = service.generarDashboardPdf(periodo);
        return archivoDescargable(archivo, MediaType.APPLICATION_PDF, "dashboard-" + periodo + ".pdf");
    }

    private ResponseEntity<byte[]> archivoDescargable(byte[] archivo, MediaType tipo, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(archivo);
    }
}

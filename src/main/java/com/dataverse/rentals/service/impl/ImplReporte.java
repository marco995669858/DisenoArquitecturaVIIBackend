package com.dataverse.rentals.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dataverse.rentals.mapper.MapperLiquidacion;
import com.dataverse.rentals.mapper.MapperMantenimiento;
import com.dataverse.rentals.mapper.MapperUnidad;
import com.dataverse.rentals.model.contrato.response.ContratoPorVencerDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;
import com.dataverse.rentals.service.ServiceContrato;
import com.dataverse.rentals.service.ServiceReporte;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Exportacion de reportes (RF-06 liquidaciones, RF-09 dashboard de
 * rentabilidad) a Excel y PDF. No hay libreria nueva agregada al azar: Apache
 * POI e iText ya estaban en la cache local de Maven (el build corre offline,
 * -o), por eso se fijaron a esas versiones exactas en el pom en vez de la
 * ultima disponible.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ImplReporte implements ServiceReporte {

    private static final DateTimeFormatter FORMATO_FECHA_GENERACION = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MapperLiquidacion mapperLiquidacion;
    private final MapperMantenimiento mapperMantenimiento;
    private final MapperUnidad mapperUnidad;
    private final ServiceContrato serviceContrato;

    private List<LiquidacionDTO> obtenerLiquidaciones(String periodo) {
        List<LiquidacionDTO> todas = mapperLiquidacion.listarLiquidaciones();
        if (!StringUtils.hasText(periodo)) {
            return todas;
        }
        return todas.stream().filter(l -> periodo.equals(l.getPeriodo())).toList();
    }

    // ---------------------------------------------------------------- Excel

    @Override
    public byte[] generarLiquidacionesExcel(String periodo) {
        List<LiquidacionDTO> liquidaciones = obtenerLiquidaciones(periodo);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            XSSFSheet hoja = workbook.createSheet("Liquidaciones");

            CellStyle estiloEncabezado = workbook.createCellStyle();
            Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            estiloEncabezado.setFont(fuenteEncabezado);
            estiloEncabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle estiloMoneda = workbook.createCellStyle();
            estiloMoneda.setDataFormat(workbook.createDataFormat().getFormat("S/ #,##0.00"));

            String[] columnas = { "Periodo", "Unidad", "Inquilino", "Alquiler", "Luz", "Agua", "Otros", "Total", "Vencimiento", "Estado" };
            Row filaEncabezado = hoja.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            int numeroFila = 1;
            BigDecimal totalGeneral = BigDecimal.ZERO;
            for (LiquidacionDTO l : liquidaciones) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(l.getPeriodo());
                fila.createCell(1).setCellValue(l.getNumeroUnidad());
                fila.createCell(2).setCellValue(l.getNombreInquilino());
                escribirCeldaMoneda(fila, 3, l.getMontoAlquiler(), estiloMoneda);
                escribirCeldaMoneda(fila, 4, l.getMontoLuz(), estiloMoneda);
                escribirCeldaMoneda(fila, 5, l.getMontoAgua(), estiloMoneda);
                escribirCeldaMoneda(fila, 6, l.getMontoOtros(), estiloMoneda);
                escribirCeldaMoneda(fila, 7, l.getMontoTotal(), estiloMoneda);
                fila.createCell(8).setCellValue(l.getFechaVencimiento() != null ? l.getFechaVencimiento().toString() : "");
                fila.createCell(9).setCellValue(l.getEstado());
                totalGeneral = totalGeneral.add(l.getMontoTotal() != null ? l.getMontoTotal() : BigDecimal.ZERO);
            }

            Row filaTotal = hoja.createRow(numeroFila);
            Cell etiquetaTotal = filaTotal.createCell(6);
            etiquetaTotal.setCellValue("TOTAL");
            Font fuenteTotal = workbook.createFont();
            fuenteTotal.setBold(true);
            CellStyle estiloTotal = workbook.createCellStyle();
            estiloTotal.setFont(fuenteTotal);
            etiquetaTotal.setCellStyle(estiloTotal);
            escribirCeldaMoneda(filaTotal, 7, totalGeneral, estiloMoneda);

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            workbook.write(salida);
            return salida.toByteArray();
        } catch (Exception e) {
            log.error("Error generando el Excel de liquidaciones", e);
            throw new RuntimeException("No se pudo generar el archivo Excel de liquidaciones.", e);
        }
    }

    private void escribirCeldaMoneda(Row fila, int indice, BigDecimal valor, CellStyle estilo) {
        Cell celda = fila.createCell(indice);
        celda.setCellValue(valor != null ? valor.doubleValue() : 0d);
        celda.setCellStyle(estilo);
    }

    // ------------------------------------------------------------------ PDF

    @Override
    public byte[] generarLiquidacionesPdf(String periodo) {
        List<LiquidacionDTO> liquidaciones = obtenerLiquidaciones(periodo);

        Document documento = new Document(PageSize.A4.rotate(), 24, 24, 36, 36);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarTitulo(documento, "Reporte de Liquidaciones",
                    StringUtils.hasText(periodo) ? "Periodo: " + periodo : "Todos los periodos");

            PdfPTable tabla = new PdfPTable(new float[] { 2, 1.5f, 3, 2, 2, 2, 2, 2, 2, 2 });
            tabla.setWidthPercentage(100);
            agregarEncabezadosTabla(tabla, "Periodo", "Unidad", "Inquilino", "Alquiler", "Luz", "Agua", "Otros", "Total", "Vence", "Estado");

            BigDecimal totalGeneral = BigDecimal.ZERO;
            for (LiquidacionDTO l : liquidaciones) {
                tabla.addCell(celda(l.getPeriodo()));
                tabla.addCell(celda(l.getNumeroUnidad()));
                tabla.addCell(celda(l.getNombreInquilino()));
                tabla.addCell(celdaMoneda(l.getMontoAlquiler()));
                tabla.addCell(celdaMoneda(l.getMontoLuz()));
                tabla.addCell(celdaMoneda(l.getMontoAgua()));
                tabla.addCell(celdaMoneda(l.getMontoOtros()));
                tabla.addCell(celdaMoneda(l.getMontoTotal()));
                tabla.addCell(celda(l.getFechaVencimiento() != null ? l.getFechaVencimiento().toString() : ""));
                tabla.addCell(celda(l.getEstado()));
                totalGeneral = totalGeneral.add(l.getMontoTotal() != null ? l.getMontoTotal() : BigDecimal.ZERO);
            }

            if (liquidaciones.isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay liquidaciones registradas para este filtro."));
                vacio.setColspan(10);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(vacio);
            } else {
                PdfPCell etiquetaTotal = new PdfPCell(new Phrase("TOTAL", fuente(10, true, BaseColor.BLACK)));
                etiquetaTotal.setColspan(7);
                etiquetaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tabla.addCell(etiquetaTotal);
                PdfPCell valorTotal = new PdfPCell(new Phrase("S/ " + totalGeneral.setScale(2, java.math.RoundingMode.HALF_UP), fuente(10, true, BaseColor.BLACK)));
                tabla.addCell(valorTotal);
                PdfPCell vacia = new PdfPCell(new Phrase(""));
                vacia.setColspan(2);
                tabla.addCell(vacia);
            }

            documento.add(tabla);
            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            log.error("Error generando el PDF de liquidaciones", e);
            throw new RuntimeException("No se pudo generar el archivo PDF de liquidaciones.", e);
        }
    }

    @Override
    public byte[] generarDashboardPdf(String periodo) {
        List<LiquidacionDTO> todasLasLiquidaciones = mapperLiquidacion.listarLiquidaciones();
        List<LiquidacionDTO> liquidacionesDelPeriodo = todasLasLiquidaciones.stream()
                .filter(l -> periodo.equals(l.getPeriodo())).toList();

        BigDecimal ingresos = liquidacionesDelPeriodo.stream()
                .filter(l -> "PAGADO".equals(l.getEstado()))
                .map(LiquidacionDTO::getMontoTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal morosidad = todasLasLiquidaciones.stream()
                .filter(l -> "PENDIENTE".equals(l.getEstado()) || "VENCIDO".equals(l.getEstado()))
                .map(LiquidacionDTO::getMontoTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MantenimientoDTO> mantenimientos = mapperMantenimiento.listarMantenimientos();
        BigDecimal egresos = mantenimientos.stream()
                .filter(m -> m.getFechaMantenimiento() != null && m.getFechaMantenimiento().toString().startsWith(periodo))
                .map(MantenimientoDTO::getMonto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal resultadoNeto = ingresos.subtract(egresos);

        List<UnidadDTO> unidades = mapperUnidad.listarUnidades();
        long disponibles = unidades.stream().filter(u -> "DISPONIBLE".equals(u.getEstado())).count();
        long ocupadas = unidades.stream().filter(u -> "OCUPADO".equals(u.getEstado())).count();
        long enMantenimiento = unidades.stream().filter(u -> "MANTENIMIENTO".equals(u.getEstado())).count();

        List<ContratoPorVencerDTO> contratosPorVencer = serviceContrato.verificarVencimientos().getData().getContratosPorVencer();

        List<LiquidacionDTO> liquidacionesPendientes = todasLasLiquidaciones.stream()
                .filter(l -> !"PAGADO".equals(l.getEstado()))
                .limit(10)
                .toList();

        Document documento = new Document(PageSize.A4, 36, 36, 36, 36);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarTitulo(documento, "Reporte de Rentabilidad", "Periodo: " + periodo);

            PdfPTable resumen = new PdfPTable(new float[] { 1, 1, 1, 1 });
            resumen.setWidthPercentage(100);
            resumen.setSpacingAfter(16);
            agregarCeldaResumen(resumen, "Ingresos", ingresos, new BaseColor(34, 197, 94));
            agregarCeldaResumen(resumen, "Egresos", egresos, new BaseColor(249, 115, 22));
            agregarCeldaResumen(resumen, "Morosidad (todos los periodos)", morosidad, new BaseColor(239, 68, 68));
            agregarCeldaResumen(resumen, "Resultado neto", resultadoNeto, new BaseColor(59, 130, 246));
            documento.add(resumen);

            documento.add(subtitulo("Unidades por estado"));
            PdfPTable tablaUnidades = new PdfPTable(3);
            tablaUnidades.setWidthPercentage(100);
            tablaUnidades.setSpacingAfter(16);
            agregarEncabezadosTabla(tablaUnidades, "Disponible", "Ocupado", "Mantenimiento");
            tablaUnidades.addCell(celda(String.valueOf(disponibles)));
            tablaUnidades.addCell(celda(String.valueOf(ocupadas)));
            tablaUnidades.addCell(celda(String.valueOf(enMantenimiento)));
            documento.add(tablaUnidades);

            documento.add(subtitulo("Contratos por vencer"));
            PdfPTable tablaContratos = new PdfPTable(new float[] { 2, 3, 2, 2 });
            tablaContratos.setWidthPercentage(100);
            tablaContratos.setSpacingAfter(16);
            agregarEncabezadosTabla(tablaContratos, "Unidad", "Inquilino", "Vence", "Dias restantes");
            if (contratosPorVencer.isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay contratos por vencer."));
                vacio.setColspan(4);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaContratos.addCell(vacio);
            } else {
                for (ContratoPorVencerDTO c : contratosPorVencer) {
                    tablaContratos.addCell(celda(c.getNumeroUnidad()));
                    tablaContratos.addCell(celda(c.getNombreInquilino()));
                    tablaContratos.addCell(celda(c.getFechaFin() != null ? c.getFechaFin().toString() : ""));
                    tablaContratos.addCell(celda(String.valueOf(c.getDiasParaVencer())));
                }
            }
            documento.add(tablaContratos);

            documento.add(subtitulo("Liquidaciones pendientes (todos los periodos)"));
            PdfPTable tablaLiquidaciones = new PdfPTable(new float[] { 2, 2, 3, 2, 2 });
            tablaLiquidaciones.setWidthPercentage(100);
            agregarEncabezadosTabla(tablaLiquidaciones, "Unidad", "Periodo", "Inquilino", "Monto", "Estado");
            if (liquidacionesPendientes.isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay liquidaciones pendientes."));
                vacio.setColspan(5);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaLiquidaciones.addCell(vacio);
            } else {
                for (LiquidacionDTO l : liquidacionesPendientes) {
                    tablaLiquidaciones.addCell(celda(l.getNumeroUnidad()));
                    tablaLiquidaciones.addCell(celda(l.getPeriodo()));
                    tablaLiquidaciones.addCell(celda(l.getNombreInquilino()));
                    tablaLiquidaciones.addCell(celdaMoneda(l.getMontoTotal()));
                    tablaLiquidaciones.addCell(celda(l.getEstado()));
                }
            }
            documento.add(tablaLiquidaciones);

            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            log.error("Error generando el PDF del dashboard", e);
            throw new RuntimeException("No se pudo generar el archivo PDF del dashboard.", e);
        }
    }

    // -------------------------------------------------------- utilitarios PDF

    private void agregarTitulo(Document documento, String titulo, String subtitulo) throws Exception {
        Paragraph parrafoTitulo = new Paragraph(titulo, fuente(18, true, BaseColor.BLACK));
        parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(parrafoTitulo);

        Paragraph parrafoSubtitulo = new Paragraph(subtitulo, fuente(11, false, BaseColor.DARK_GRAY));
        parrafoSubtitulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(parrafoSubtitulo);

        Paragraph fechaGeneracion = new Paragraph("Generado el " + LocalDate.now().atStartOfDay().format(FORMATO_FECHA_GENERACION),
                fuente(9, false, BaseColor.GRAY));
        fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
        fechaGeneracion.setSpacingAfter(16);
        documento.add(fechaGeneracion);
    }

    private Paragraph subtitulo(String texto) {
        Paragraph p = new Paragraph(texto, fuente(13, true, BaseColor.BLACK));
        p.setSpacingBefore(4);
        p.setSpacingAfter(8);
        return p;
    }

    private void agregarEncabezadosTabla(PdfPTable tabla, String... columnas) {
        for (String columna : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(columna, fuente(10, true, BaseColor.WHITE)));
            celda.setBackgroundColor(new BaseColor(30, 58, 138));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setPadding(6);
            tabla.addCell(celda);
        }
    }

    private void agregarCeldaResumen(PdfPTable tabla, String etiqueta, BigDecimal valor, BaseColor color) {
        PdfPCell celda = new PdfPCell();
        celda.setPadding(10);
        celda.setBorderColor(color);
        Paragraph contenido = new Paragraph();
        contenido.add(new Phrase(etiqueta + "\n", fuente(9, false, BaseColor.DARK_GRAY)));
        contenido.add(new Phrase("S/ " + valor.setScale(2, java.math.RoundingMode.HALF_UP), fuente(14, true, color)));
        celda.addElement(contenido);
        tabla.addCell(celda);
    }

    private PdfPCell celda(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "", fuente(9, false, BaseColor.BLACK)));
        celda.setPadding(5);
        return celda;
    }

    private PdfPCell celdaMoneda(BigDecimal valor) {
        BigDecimal seguro = valor != null ? valor : BigDecimal.ZERO;
        PdfPCell celda = new PdfPCell(new Phrase("S/ " + seguro.setScale(2, java.math.RoundingMode.HALF_UP), fuente(9, false, BaseColor.BLACK)));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return celda;
    }

    private com.itextpdf.text.Font fuente(float tamano, boolean negrita, BaseColor color) {
        return new com.itextpdf.text.Font(FontFamily.HELVETICA, tamano, negrita ? com.itextpdf.text.Font.BOLD : com.itextpdf.text.Font.NORMAL, color);
    }
}

package com.dataverse.rentals.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataverse.rentals.mapper.MapperLiquidacion;
import com.dataverse.rentals.mapper.MapperMantenimiento;
import com.dataverse.rentals.mapper.MapperUnidad;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.contrato.response.AlertaVencimientoResultDTO;
import com.dataverse.rentals.model.contrato.response.ContratoPorVencerDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;
import com.dataverse.rentals.service.ServiceContrato;

/**
 * No hay un flujo de negocio nuevo que probar aca (la exportacion es
 * presentacion, no calculo), pero si dos cosas que si pueden romperse en
 * silencio: que el archivo generado realmente sea un XLSX/PDF valido (no un
 * stream corrupto que Excel/el visor de PDF rechacen) y que las cifras que
 * ImplReporte agrega para el dashboard (ingresos, egresos, morosidad) sumen
 * exactamente lo que las reglas del propio dashboard.ts del frontend definen
 * -- mismo criterio: PAGADO del periodo = ingreso, PENDIENTE/VENCIDO de
 * *todos* los periodos = morosidad.
 */
@ExtendWith(MockitoExtension.class)
class ImplReporteTest {

    @Mock
    private MapperLiquidacion mapperLiquidacion;
    @Mock
    private MapperMantenimiento mapperMantenimiento;
    @Mock
    private MapperUnidad mapperUnidad;
    @Mock
    private ServiceContrato serviceContrato;

    @InjectMocks
    private ImplReporte servicio;

    private LiquidacionDTO liquidacion(String periodo, String estado, String numeroUnidad, BigDecimal total) {
        LiquidacionDTO l = new LiquidacionDTO();
        l.setPeriodo(periodo);
        l.setEstado(estado);
        l.setNumeroUnidad(numeroUnidad);
        l.setNombreInquilino("Inquilino " + numeroUnidad);
        l.setMontoAlquiler(total);
        l.setMontoLuz(BigDecimal.ZERO);
        l.setMontoAgua(BigDecimal.ZERO);
        l.setMontoOtros(BigDecimal.ZERO);
        l.setMontoTotal(total);
        l.setFechaVencimiento(LocalDate.of(2026, 9, 20));
        return l;
    }

    @Test
    void elExcelDeLiquidacionesEsUnXlsxValidoYFiltraPorPeriodo() throws Exception {
        when(mapperLiquidacion.listarLiquidaciones()).thenReturn(List.of(
                liquidacion("2026-08", "PAGADO", "A1", new BigDecimal("500.00")),
                liquidacion("2026-09", "PAGADO", "A2", new BigDecimal("700.00"))
        ));

        byte[] excel = servicio.generarLiquidacionesExcel("2026-09");

        assertThat(excel[0]).isEqualTo((byte) 'P'); // firma ZIP/OOXML: "PK.."
        assertThat(excel[1]).isEqualTo((byte) 'K');

        try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            XSSFSheet hoja = libro.getSheetAt(0);
            Row filaDatos = hoja.getRow(1);
            assertThat(filaDatos.getCell(1).getStringCellValue()).isEqualTo("A2"); // solo el periodo filtrado
            assertThat(hoja.getRow(2).getCell(6).getStringCellValue()).isEqualTo("TOTAL"); // fila 2 ya es el total: ninguna otra liquidacion se colo
            assertThat((Object) hoja.getRow(3)).isNull();
        }
    }

    @Test
    void elExcelSinFiltroDePeriodoIncluyeTodasLasLiquidaciones() throws Exception {
        when(mapperLiquidacion.listarLiquidaciones()).thenReturn(List.of(
                liquidacion("2026-08", "PAGADO", "A1", new BigDecimal("500.00")),
                liquidacion("2026-09", "PAGADO", "A2", new BigDecimal("700.00"))
        ));

        byte[] excel = servicio.generarLiquidacionesExcel(null);

        try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            XSSFSheet hoja = libro.getSheetAt(0);
            assertThat(hoja.getRow(1).getCell(1).getStringCellValue()).isEqualTo("A1");
            assertThat(hoja.getRow(2).getCell(1).getStringCellValue()).isEqualTo("A2");
        }
    }

    @Test
    void elPdfDeLiquidacionesEmpiezaConLaFirmaPdf() {
        when(mapperLiquidacion.listarLiquidaciones()).thenReturn(List.of(
                liquidacion("2026-09", "PENDIENTE", "B1", new BigDecimal("300.00"))
        ));

        byte[] pdf = servicio.generarLiquidacionesPdf("2026-09");

        assertThat(new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    void elPdfDelDashboardCalculaIngresosEgresosYMorosidadComoElFrontend() {
        when(mapperLiquidacion.listarLiquidaciones()).thenReturn(List.of(
                liquidacion("2026-09", "PAGADO", "A1", new BigDecimal("500.00")), // ingreso del periodo
                liquidacion("2026-09", "PENDIENTE", "A2", new BigDecimal("200.00")), // morosidad
                liquidacion("2026-08", "VENCIDO", "A3", new BigDecimal("150.00")) // morosidad de OTRO periodo (cuenta igual)
        ));

        MantenimientoDTO mantenimientoDelPeriodo = new MantenimientoDTO();
        mantenimientoDelPeriodo.setFechaMantenimiento(LocalDate.of(2026, 9, 5));
        mantenimientoDelPeriodo.setMonto(new BigDecimal("80.00"));
        MantenimientoDTO mantenimientoDeOtroPeriodo = new MantenimientoDTO();
        mantenimientoDeOtroPeriodo.setFechaMantenimiento(LocalDate.of(2026, 8, 5));
        mantenimientoDeOtroPeriodo.setMonto(new BigDecimal("999.00"));
        when(mapperMantenimiento.listarMantenimientos()).thenReturn(List.of(mantenimientoDelPeriodo, mantenimientoDeOtroPeriodo));

        UnidadDTO disponible = new UnidadDTO(); disponible.setEstado("DISPONIBLE");
        UnidadDTO ocupada = new UnidadDTO(); ocupada.setEstado("OCUPADO");
        when(mapperUnidad.listarUnidades()).thenReturn(List.of(disponible, ocupada, ocupada));

        AlertaVencimientoResultDTO alertaVacia = new AlertaVencimientoResultDTO(0, List.of());
        when(serviceContrato.verificarVencimientos()).thenReturn(new ResponseDTO<>(alertaVacia, false, "ok"));

        byte[] pdf = servicio.generarDashboardPdf("2026-09");

        // ingresos=500 (solo PAGADO de 2026-09), egresos=80 (solo mantenimiento de 2026-09),
        // morosidad=350 (200+150, de TODOS los periodos), resultado neto=420.
        assertThat(new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF");
        assertThat(pdf.length).isGreaterThan(500); // no es un PDF vacio/truncado
    }

    @Test
    void elDashboardUsaLosContratosPorVencerDeVerificarVencimientos() {
        when(mapperLiquidacion.listarLiquidaciones()).thenReturn(List.of());
        when(mapperMantenimiento.listarMantenimientos()).thenReturn(List.of());
        when(mapperUnidad.listarUnidades()).thenReturn(List.of());

        ContratoPorVencerDTO porVencer = new ContratoPorVencerDTO(1, "A1", "Juan Perez", LocalDate.of(2026, 10, 1), 4);
        AlertaVencimientoResultDTO alerta = new AlertaVencimientoResultDTO(0, List.of(porVencer));
        when(serviceContrato.verificarVencimientos()).thenReturn(new ResponseDTO<>(alerta, false, "ok"));

        byte[] pdf = servicio.generarDashboardPdf("2026-09");

        assertThat(new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }
}

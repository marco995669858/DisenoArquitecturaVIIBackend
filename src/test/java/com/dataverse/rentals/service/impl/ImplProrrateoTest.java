package com.dataverse.rentals.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.mapper.MapperProrrateo;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.prorrateo.request.CalcularProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.response.ConsumoUnidadDTO;
import com.dataverse.rentals.model.prorrateo.response.ProrrateoDTO;
import com.dataverse.rentals.security.AuditContext;

/**
 * CU-05 -- Calcular Prorrateo. Se prueba con los mappers mockeados (sin base
 * de datos real): lo que interesa verificar aca es la formula del reparto
 * -- que la unidad que va al final absorba el resto del redondeo, para que
 * la suma de lo asignado cierre exacto con el excedente -- y las
 * precondiciones (RN-01, FA-01, FA-02) que bloquean el calculo.
 */
@ExtendWith(MockitoExtension.class)
class ImplProrrateoTest {

    @Mock
    private MapperProrrateo mapper;
    @Mock
    private MapperPeriodoFacturacion mapperPeriodo;
    @Mock
    private MapperParametroSistema mapperParametro;
    @Mock
    private AuditContext audit;

    @InjectMocks
    private ImplProrrateo servicio;

    private CalcularProrrateoRequest request(String periodo, String tipoServicio) {
        CalcularProrrateoRequest req = new CalcularProrrateoRequest();
        req.setPeriodo(periodo);
        req.setTipoServicio(tipoServicio);
        return req;
    }

    // audit.usuarioActual()/ipActual() solo se llaman una vez pasadas todas
    // las precondiciones (RN-01/FA-01/FA-02); se stubean por caso, no en un
    // @BeforeEach comun, para no disparar UnnecessaryStubbingException en
    // los tests que retornan error antes de llegar ahi.
    private void stubAuditoria() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");
    }

    @Test
    void rechazaCuandoFaltaElPeriodoOElServicio() {
        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("", "LUZ"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("periodo");
    }

    @Test
    void rechazaCuandoNoHayLecturaDelMedidorGeneral() {
        // COALESCE(..., -1) en el mapper: -1 es el centinela de "no hay lectura".
        when(mapper.obtenerConsumoGeneral("2026-09", "LUZ")).thenReturn(new BigDecimal("-1"));

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "LUZ"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("medidor general");
    }

    @Test
    void rechazaPeriodoIncompletoFA02() {
        when(mapper.obtenerConsumoGeneral("2026-09", "LUZ")).thenReturn(new BigDecimal("500"));
        when(mapper.contarUnidadesConMedidorIndividual("LUZ")).thenReturn(3);
        when(mapper.contarUnidadesConLecturaIndividual("2026-09", "LUZ")).thenReturn(2); // falta una

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "LUZ"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("FA-02");
    }

    @Test
    void rechazaCuandoNoHayReciboDelServicio() {
        when(mapper.obtenerConsumoGeneral("2026-09", "LUZ")).thenReturn(new BigDecimal("500"));
        when(mapper.contarUnidadesConMedidorIndividual("LUZ")).thenReturn(1);
        when(mapper.contarUnidadesConLecturaIndividual("2026-09", "LUZ")).thenReturn(1);
        when(mapper.obtenerMontoTotalRecibo("2026-09", "LUZ")).thenReturn(null);

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "LUZ"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("recibo");
    }

    @Test
    void rechazaExcedenteNegativoFA01() {
        // La suma de lecturas individuales no puede superar al medidor general.
        when(mapper.obtenerConsumoGeneral("2026-09", "LUZ")).thenReturn(new BigDecimal("50"));
        when(mapper.contarUnidadesConMedidorIndividual("LUZ")).thenReturn(1);
        when(mapper.contarUnidadesConLecturaIndividual("2026-09", "LUZ")).thenReturn(1);
        when(mapper.obtenerMontoTotalRecibo("2026-09", "LUZ")).thenReturn(new BigDecimal("100"));
        when(mapper.obtenerIdRecibo("2026-09", "LUZ")).thenReturn(1);
        when(mapper.listarConsumosIndividuales("2026-09", "LUZ")).thenReturn(List.of(
                new ConsumoUnidadDTO(1, "A", new BigDecimal("40"), new BigDecimal("60")) // 60 > 50
        ));

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "LUZ"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("FA-01");
    }

    @Test
    void reparteElExcedenteProporcionalAlConsumoYLaUltimaUnidadAbsorbeElRedondeo() {
        // consumoGeneral 100, 3 unidades con 10 cada una -> excedente 70,
        // repartido a tercios: 23.33 / 23.33 / 23.34 (el ultimo se lleva el
        // centavo que sobra para que la suma cierre exacto en 70.00).
        when(mapper.obtenerConsumoGeneral("2026-09", "LUZ")).thenReturn(new BigDecimal("100"));
        when(mapper.contarUnidadesConMedidorIndividual("LUZ")).thenReturn(3);
        when(mapper.contarUnidadesConLecturaIndividual("2026-09", "LUZ")).thenReturn(3);
        when(mapper.obtenerMontoTotalRecibo("2026-09", "LUZ")).thenReturn(new BigDecimal("200"));
        when(mapper.obtenerIdRecibo("2026-09", "LUZ")).thenReturn(9);
        when(mapper.listarConsumosIndividuales("2026-09", "LUZ")).thenReturn(List.of(
                new ConsumoUnidadDTO(1, "A", new BigDecimal("40"), new BigDecimal("10")),
                new ConsumoUnidadDTO(2, "B", new BigDecimal("40"), new BigDecimal("10")),
                new ConsumoUnidadDTO(3, "C", new BigDecimal("40"), new BigDecimal("10"))
        ));
        when(mapperParametro.obtenerValorPorClave("CRITERIO_PRORRATEO")).thenReturn(null); // por defecto: CONSUMO
        when(mapper.buscarIdProrrateoExistente(anyInt(), anyString(), anyString())).thenReturn(null);
        stubAuditoria();

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "LUZ"));

        assertThat(resultado.isError()).isFalse();
        List<ProrrateoDTO> filas = resultado.getData();
        assertThat(filas).hasSize(3);

        assertThat(filas.get(0).getConsumoComunAsignado()).isEqualByComparingTo("23.33");
        assertThat(filas.get(1).getConsumoComunAsignado()).isEqualByComparingTo("23.33");
        assertThat(filas.get(2).getConsumoComunAsignado()).isEqualByComparingTo("23.34"); // absorbe el redondeo

        BigDecimal sumaExcedenteRepartido = filas.stream()
                .map(ProrrateoDTO::getConsumoComunAsignado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(sumaExcedenteRepartido).isEqualByComparingTo("70.00");

        assertThat(filas.get(0).getCostoUnitario()).isEqualByComparingTo("2.0000"); // 200 / 100
        assertThat(filas.get(0).getMontoCalculado()).isEqualByComparingTo("66.66"); // (10+23.33)*2
        assertThat(filas.get(2).getMontoCalculado()).isEqualByComparingTo("66.68"); // (10+23.34)*2

        BigDecimal sumaMontos = filas.stream()
                .map(ProrrateoDTO::getMontoCalculado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(sumaMontos).isEqualByComparingTo(new BigDecimal("200")); // cierra con el recibo
    }

    @Test
    void repartePorAreaCuandoElCriterioConfiguradoEsArea() {
        // S-04: si CRITERIO_PRORRATEO=AREA, el reparto ignora el consumo
        // individual y usa el area de cada unidad como base.
        when(mapper.obtenerConsumoGeneral("2026-09", "AGUA")).thenReturn(new BigDecimal("60"));
        when(mapper.contarUnidadesConMedidorIndividual("AGUA")).thenReturn(2);
        when(mapper.contarUnidadesConLecturaIndividual("2026-09", "AGUA")).thenReturn(2);
        when(mapper.obtenerMontoTotalRecibo("2026-09", "AGUA")).thenReturn(new BigDecimal("60"));
        when(mapper.obtenerIdRecibo("2026-09", "AGUA")).thenReturn(5);
        // Consumo individual identico (10 y 10) para que la unica diferencia
        // en el reparto del excedente venga del area (30 vs 90 m2).
        when(mapper.listarConsumosIndividuales("2026-09", "AGUA")).thenReturn(List.of(
                new ConsumoUnidadDTO(1, "A", new BigDecimal("30"), new BigDecimal("10")),
                new ConsumoUnidadDTO(2, "B", new BigDecimal("90"), new BigDecimal("10"))
        ));
        when(mapperParametro.obtenerValorPorClave("CRITERIO_PRORRATEO")).thenReturn("AREA");
        when(mapper.buscarIdProrrateoExistente(anyInt(), anyString(), anyString())).thenReturn(null);
        stubAuditoria();

        ResponseDTO<List<ProrrateoDTO>> resultado = servicio.calcularProrrateo(request("2026-09", "AGUA"));

        assertThat(resultado.isError()).isFalse();
        List<ProrrateoDTO> filas = resultado.getData();
        // excedente = 60 - 20 = 40; base total de area = 120; A (30/120) = 10.00
        assertThat(filas.get(0).getConsumoComunAsignado()).isEqualByComparingTo("10.00");
        // B es la ultima: se lleva el resto (40 - 10.00 = 30.00), coincida o no con 90/120*40=30 exacto
        assertThat(filas.get(1).getConsumoComunAsignado()).isEqualByComparingTo("30.00");
    }
}

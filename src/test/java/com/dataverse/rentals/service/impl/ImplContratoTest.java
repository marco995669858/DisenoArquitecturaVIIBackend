package com.dataverse.rentals.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dataverse.rentals.mapper.MapperContrato;
import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.mapper.MapperUnidad;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.contrato.request.ContratoRequest;
import com.dataverse.rentals.model.contrato.request.RenovarContratoRequest;
import com.dataverse.rentals.model.contrato.response.AlertaVencimientoResultDTO;
import com.dataverse.rentals.model.contrato.response.ContratoDTO;
import com.dataverse.rentals.model.contrato.response.RenovarContratoResultDTO;
import com.dataverse.rentals.security.AuditContext;

/**
 * unidad.estado ya no se toca a mano para OCUPADO: ImplContrato lo sincroniza
 * solo con el estado del contrato. Estas pruebas verifican esa sincronizacion
 * (armada en esta misma sesion) en los casos donde es mas facil que se
 * cuele un bug: crear, editar cambiando de unidad, editar sin cambiar de
 * unidad, y eliminar.
 */
@ExtendWith(MockitoExtension.class)
class ImplContratoTest {

    @Mock
    private MapperContrato mapper;
    @Mock
    private MapperParametroSistema mapperParametro;
    @Mock
    private MapperUnidad mapperUnidad;
    @Mock
    private AuditContext audit;

    @InjectMocks
    private ImplContrato servicio;

    private ContratoRequest contrato(Integer idContrato, Integer idUnidad, String estado) {
        ContratoRequest req = new ContratoRequest();
        req.setIdContrato(idContrato);
        req.setIdUnidad(idUnidad);
        req.setEstado(estado);
        return req;
    }

    private ContratoDTO contratoExistente(String estado, LocalDate fechaFin, BigDecimal montoAlquiler) {
        return new ContratoDTO(4, 10, "A1", 7, "Juan Perez", LocalDate.of(2026, 1, 1), fechaFin, montoAlquiler, "/contratos/4.pdf", estado);
    }

    @Test
    void alCrearUnContratoVigenteMarcaLaUnidadOcupada() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<Integer> resultado = servicio.insertUpdateContrato(contrato(null, 10, "VIGENTE"));

        assertThat(resultado.isError()).isFalse();
        verify(mapper).insertarContrato(any(), eq(1), eq("127.0.0.1"));
        verify(mapperUnidad).marcarUnidadOcupada(10, 1, "127.0.0.1");
        verify(mapperUnidad, never()).marcarUnidadDisponible(anyInt(), anyInt(), anyString());
        // contrato nuevo: no hay "unidad anterior" que consultar.
        verify(mapper, never()).obtenerIdUnidadDeContrato(anyInt());
    }

    @Test
    void alCrearUnContratoNoVigenteDejaLaUnidadDisponible() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        servicio.insertUpdateContrato(contrato(null, 10, "FINALIZADO"));

        verify(mapperUnidad).marcarUnidadDisponible(10, 1, "127.0.0.1");
        verify(mapperUnidad, never()).marcarUnidadOcupada(anyInt(), anyInt(), anyString());
    }

    @Test
    void alEditarUnContratoVigenteSinCambiarDeUnidadNoTocaOtraUnidad() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");
        when(mapper.obtenerIdUnidadDeContrato(4)).thenReturn(10); // misma unidad de siempre

        servicio.insertUpdateContrato(contrato(4, 10, "VIGENTE"));

        verify(mapperUnidad).marcarUnidadOcupada(10, 1, "127.0.0.1");
        // no debe liberar nada: la unidad "anterior" es la misma que la nueva.
        verify(mapperUnidad, never()).marcarUnidadDisponible(anyInt(), anyInt(), anyString());
    }

    @Test
    void alMoverUnContratoVigenteDeUnidadLiberaLaAnteriorYOcupaLaNueva() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");
        when(mapper.obtenerIdUnidadDeContrato(4)).thenReturn(10); // unidad vieja

        servicio.insertUpdateContrato(contrato(4, 20, "VIGENTE")); // unidad nueva

        verify(mapperUnidad).marcarUnidadOcupada(20, 1, "127.0.0.1");
        verify(mapperUnidad).marcarUnidadDisponible(10, 1, "127.0.0.1");
    }

    @Test
    void alEditarUnContratoParaQueDejeDeEstarVigenteLiberaLaUnidad() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");
        when(mapper.obtenerIdUnidadDeContrato(4)).thenReturn(10);

        servicio.insertUpdateContrato(contrato(4, 10, "VENCIDO"));

        verify(mapperUnidad).marcarUnidadDisponible(10, 1, "127.0.0.1");
        verify(mapperUnidad, never()).marcarUnidadOcupada(anyInt(), anyInt(), anyString());
    }

    @Test
    void noPermiteEliminarUnContratoConLiquidacionesAsociadas() {
        when(mapper.contarLiquidacionesDeContrato(4)).thenReturn(2);

        ResponseDTO<Integer> resultado = servicio.eliminarContrato(contrato(4, 10, "VIGENTE"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("liquidaciones");
        verify(mapper, never()).eliminarContrato(anyInt(), any(), any());
        verifyNoInteractions(mapperUnidad);
    }

    @Test
    void alEliminarUnContratoLiberaSuUnidad() {
        when(mapper.contarLiquidacionesDeContrato(4)).thenReturn(0);
        when(mapper.obtenerIdUnidadDeContrato(4)).thenReturn(10);
        when(mapper.eliminarContrato(eq(4), any(), any())).thenReturn(1);
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<Integer> resultado = servicio.eliminarContrato(contrato(4, 10, "VIGENTE"));

        assertThat(resultado.isError()).isFalse();
        verify(mapperUnidad).marcarUnidadDisponible(10, 1, "127.0.0.1");
    }

    @Test
    void verificarVencimientosLiberaLasUnidadesAntesDeMarcarLosContratosVencidos() {
        when(mapperParametro.obtenerValorPorClave("DIAS_ALERTA_VENCIMIENTO")).thenReturn("15");
        when(mapper.marcarContratosVencidos(any(), any())).thenReturn(2);
        when(mapper.listarContratosPorVencer(15)).thenReturn(java.util.List.of());

        ResponseDTO<AlertaVencimientoResultDTO> resultado = servicio.verificarVencimientos();

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData().getContratosMarcadosVencidos()).isEqualTo(2);

        // El orden importa: si se invirtiera, se perderia el criterio para
        // saber que unidades liberar (ver el comentario en el mapper).
        InOrder orden = Mockito.inOrder(mapper);
        orden.verify(mapper).liberarUnidadesDeContratosPorVencer(any(), any());
        orden.verify(mapper).marcarContratosVencidos(any(), any());
    }

    @Test
    void siElParametroDeDiasNoEsUnNumeroUsaElDefaultDeTreintaDias() {
        when(mapperParametro.obtenerValorPorClave("DIAS_ALERTA_VENCIMIENTO")).thenReturn("no-es-un-numero");
        when(mapper.listarContratosPorVencer(30)).thenReturn(java.util.List.of());

        ResponseDTO<AlertaVencimientoResultDTO> resultado = servicio.verificarVencimientos();

        assertThat(resultado.isError()).isFalse();
        verify(mapper).listarContratosPorVencer(30);
    }

    // -------------------------------------------------- CU-03b: renovar contrato

    @Test
    void renovarUnContratoVigenteLoMarcaRenovadoYCreaUnoNuevoQueLoContinua() {
        ContratoDTO actual = contratoExistente("VIGENTE", LocalDate.of(2026, 9, 30), new BigDecimal("1200.00"));
        when(mapper.obtenerContratoPorId(4)).thenReturn(actual);
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        RenovarContratoRequest args = new RenovarContratoRequest();
        args.setIdContrato(4);
        args.setNuevaFechaFin(LocalDate.of(2027, 9, 30));

        ResponseDTO<RenovarContratoResultDTO> resultado = servicio.renovarContrato(args);

        assertThat(resultado.isError()).isFalse();
        verify(mapper).marcarContratoRenovado(4, 1, "127.0.0.1");

        ArgumentCaptor<ContratoRequest> captor = ArgumentCaptor.forClass(ContratoRequest.class);
        verify(mapper).insertarContrato(captor.capture(), eq(1), eq("127.0.0.1"));
        ContratoRequest nuevo = captor.getValue();
        assertThat(nuevo.getIdUnidad()).isEqualTo(10);
        assertThat(nuevo.getIdInquilino()).isEqualTo(7);
        assertThat(nuevo.getFechaInicio()).isEqualTo(LocalDate.of(2026, 9, 30)); // continua justo donde termino el anterior
        assertThat(nuevo.getFechaFin()).isEqualTo(LocalDate.of(2027, 9, 30));
        assertThat(nuevo.getMontoAlquiler()).isEqualByComparingTo("1200.00"); // heredado: no se pidio uno nuevo
        assertThat(nuevo.getEstado()).isEqualTo("VIGENTE");

        verify(mapperUnidad).marcarUnidadOcupada(10, 1, "127.0.0.1");
    }

    @Test
    void renovarConNuevoMontoUsaEseMontoEnVezDelHeredado() {
        when(mapper.obtenerContratoPorId(4)).thenReturn(contratoExistente("VENCIDO", LocalDate.of(2026, 9, 30), new BigDecimal("1200.00")));
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        RenovarContratoRequest args = new RenovarContratoRequest();
        args.setIdContrato(4);
        args.setNuevaFechaFin(LocalDate.of(2027, 9, 30));
        args.setNuevoMontoAlquiler(new BigDecimal("1350.00"));

        servicio.renovarContrato(args);

        ArgumentCaptor<ContratoRequest> captor = ArgumentCaptor.forClass(ContratoRequest.class);
        verify(mapper).insertarContrato(captor.capture(), eq(1), eq("127.0.0.1"));
        assertThat(captor.getValue().getMontoAlquiler()).isEqualByComparingTo("1350.00");
    }

    @Test
    void noPermiteRenovarUnContratoYaFinalizado() {
        when(mapper.obtenerContratoPorId(4)).thenReturn(contratoExistente("FINALIZADO", LocalDate.of(2026, 9, 30), new BigDecimal("1200.00")));

        RenovarContratoRequest args = new RenovarContratoRequest();
        args.setIdContrato(4);
        args.setNuevaFechaFin(LocalDate.of(2027, 9, 30));

        ResponseDTO<RenovarContratoResultDTO> resultado = servicio.renovarContrato(args);

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("VIGENTE o VENCIDO");
        verify(mapper, never()).marcarContratoRenovado(anyInt(), any(), any());
        verify(mapper, never()).insertarContrato(any(), any(), any());
    }

    @Test
    void noPermiteRenovarConUnaFechaFinQueNoExtiendeElContrato() {
        when(mapper.obtenerContratoPorId(4)).thenReturn(contratoExistente("VIGENTE", LocalDate.of(2026, 9, 30), new BigDecimal("1200.00")));

        RenovarContratoRequest args = new RenovarContratoRequest();
        args.setIdContrato(4);
        args.setNuevaFechaFin(LocalDate.of(2026, 9, 30)); // igual a la actual, no la extiende

        ResponseDTO<RenovarContratoResultDTO> resultado = servicio.renovarContrato(args);

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("posterior");
        verify(mapper, never()).marcarContratoRenovado(anyInt(), any(), any());
    }

    @Test
    void devuelveErrorSiElContratoARenovarNoExiste() {
        when(mapper.obtenerContratoPorId(99)).thenReturn(null);

        RenovarContratoRequest args = new RenovarContratoRequest();
        args.setIdContrato(99);
        args.setNuevaFechaFin(LocalDate.of(2027, 1, 1));

        ResponseDTO<RenovarContratoResultDTO> resultado = servicio.renovarContrato(args);

        assertThat(resultado.isError()).isTrue();
        verifyNoInteractions(mapperUnidad);
    }
}

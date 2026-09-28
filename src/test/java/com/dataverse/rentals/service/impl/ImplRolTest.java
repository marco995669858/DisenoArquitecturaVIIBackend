package com.dataverse.rentals.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataverse.rentals.mapper.MapperRol;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.rol.request.RolRequest;
import com.dataverse.rentals.security.AuditContext;

/**
 * Antes, los modulos que un rol podia ver estaban fijos en el codigo Angular
 * (arreglos de ROLE_X por ruta); un rol nuevo creado desde esta pantalla no
 * encajaba en ningun arreglo y quedaba sin acceso a nada mas que el
 * Dashboard. Ahora insertUpdateRol sincroniza los permisos del rol contra
 * rol_permiso (reemplazandolos por completo en cada guardado, como
 * refresh_token: es configuracion, no un registro de negocio con historial),
 * y cada permiso trae ademas su nivel de acceso (LECTURA/ESCRITURA), para
 * roles de solo consulta como el de una auditoria externa.
 */
@ExtendWith(MockitoExtension.class)
class ImplRolTest {

    @Mock
    private MapperRol mapper;
    @Mock
    private AuditContext audit;

    @InjectMocks
    private ImplRol servicio;

    private RolRequest rol(Integer idRol, String nombre, List<PermisoAsignadoDTO> permisos) {
        RolRequest req = new RolRequest();
        req.setIdRol(idRol);
        req.setNombre(nombre);
        req.setPermisos(permisos);
        return req;
    }

    private PermisoAsignadoDTO permiso(String codigo, String nivel) {
        return new PermisoAsignadoDTO(codigo, nivel);
    }

    @Test
    void alCrearUnRolNuevoInsertaCadaPermisoMarcadoConSuNivel() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        servicio.insertUpdateRol(rol(null, "SUPERVISOR", List.of(
                permiso("UNIDADES", "ESCRITURA"),
                permiso("MANTENIMIENTO", "LECTURA")
        )));

        verify(mapper).insertarRol(any(), eq(1), eq("127.0.0.1"));
        // se limpia primero (por si el id se reutilizara) y se insertan los 2 marcados
        verify(mapper).eliminarPermisosDeRol(null);
        verify(mapper).insertarPermiso(null, "UNIDADES", "ESCRITURA");
        verify(mapper).insertarPermiso(null, "MANTENIMIENTO", "LECTURA");
    }

    @Test
    void alEditarUnRolReemplazaTodosSusPermisosPorLosNuevos() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        servicio.insertUpdateRol(rol(5, "COBRANZAS", List.of(permiso("CONTRATOS", "ESCRITURA"))));

        verify(mapper).eliminarPermisosDeRol(5);
        verify(mapper, times(1)).insertarPermiso(eq(5), any(), any());
        verify(mapper).insertarPermiso(5, "CONTRATOS", "ESCRITURA");
    }

    @Test
    void unRolDeSoloConsultaGuardaSusPermisosComoLectura() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        servicio.insertUpdateRol(rol(6, "CONSULTA", List.of(
                permiso("UNIDADES", "LECTURA"),
                permiso("CONTRATOS", "LECTURA")
        )));

        verify(mapper).insertarPermiso(6, "UNIDADES", "LECTURA");
        verify(mapper).insertarPermiso(6, "CONTRATOS", "LECTURA");
    }

    @Test
    void unNivelDeAccesoAusenteOInvalidoFallaHaciaElLadoSeguroLectura() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        servicio.insertUpdateRol(rol(6, "CONSULTA", List.of(
                permiso("UNIDADES", null),
                permiso("CONTRATOS", "ALGO_INVALIDO")
        )));

        verify(mapper).insertarPermiso(6, "UNIDADES", "LECTURA");
        verify(mapper).insertarPermiso(6, "CONTRATOS", "LECTURA");
    }

    @Test
    void unRolSinNingunPermisoMarcadoQuedaSoloConElDashboardImplicito() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<Integer> resultado = servicio.insertUpdateRol(rol(5, "SOLO_LECTURA", List.of()));

        assertThat(resultado.isError()).isFalse();
        verify(mapper).eliminarPermisosDeRol(5);
        verify(mapper, never()).insertarPermiso(eq(5), any(), any());
    }

    @Test
    void unaListaDePermisosNulaNoRompeElGuardado() {
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<Integer> resultado = servicio.insertUpdateRol(rol(5, "SIN_PERMISOS", null));

        assertThat(resultado.isError()).isFalse();
        verify(mapper).eliminarPermisosDeRol(5);
        verify(mapper, never()).insertarPermiso(eq(5), any(), any());
    }

    @Test
    void listarPermisosDeRolDelegaEnElMapper() {
        when(mapper.listarPermisosDeRol(5)).thenReturn(List.of(
                permiso("UNIDADES", "ESCRITURA"),
                permiso("MANTENIMIENTO", "LECTURA")
        ));

        ResponseDTO<List<PermisoAsignadoDTO>> resultado = servicio.listarPermisosDeRol(5);

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData()).extracting(PermisoAsignadoDTO::getCodigo).containsExactly("UNIDADES", "MANTENIMIENTO");
        assertThat(resultado.getData()).extracting(PermisoAsignadoDTO::getNivelAcceso).containsExactly("ESCRITURA", "LECTURA");
    }
}

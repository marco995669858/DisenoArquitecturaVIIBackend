package com.dataverse.rentals.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dataverse.rentals.mapper.MapperUsuario;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.usuario.request.CambiarMiPasswordRequest;
import com.dataverse.rentals.model.usuario.request.LoginRequest;
import com.dataverse.rentals.model.usuario.request.UsuarioRequest;
import com.dataverse.rentals.model.usuario.response.AuthDTO;
import com.dataverse.rentals.model.usuario.response.EstadoMfaInternoDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioLoginDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.security.CustomUserDetails;
import com.dataverse.rentals.security.CustomUserDetailsService;
import com.dataverse.rentals.security.JwtService;
import com.dataverse.rentals.security.LoginAttemptTracker;
import com.dataverse.rentals.security.TotpUtil;

/**
 * Dos cosas se prueban aca: el resguardo de "nunca quedarse sin
 * Administrador activo" (el bug real que causo el autobloqueo que corregimos
 * en esta sesion) y las tres ramas del login con MFA obligatorio por
 * defecto (sin MFA, MFA sin confirmar todavia, MFA ya confirmado).
 */
@ExtendWith(MockitoExtension.class)
class ImplUsuarioTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomUserDetailsService detailsService;
    @Mock
    private MapperUsuario mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditContext audit;
    @Mock
    private TotpUtil totpUtil;
    @Mock
    private LoginAttemptTracker intentos;

    @InjectMocks
    private ImplUsuario servicio;

    private CustomUserDetails usuario(Integer idUsuario, String username) {
        return new CustomUserDetails(new UsuarioLoginDTO(idUsuario, username, "hash-no-relevante", true, "ROLE_ADMINISTRADOR"));
    }

    /** El guard de bloqueo corre antes que nada en login(); sin esto, cualquier prueba de login tira NPE. */
    private void sinBloqueoPrevio() {
        when(audit.ipActual()).thenReturn("127.0.0.1");
        when(intentos.tiempoRestanteBloqueo(anyString())).thenReturn(Duration.ZERO);
    }

    // ------------------------------------------------------------------
    // Login + MFA obligatorio por defecto
    // ------------------------------------------------------------------

    @Test
    void loginSinMfaHabilitadoEntregaElTokenDeUnaVez() {
        sinBloqueoPrevio();
        when(detailsService.loadUserByUsername("admin")).thenReturn(usuario(1, "admin"));
        when(mapper.obtenerEstadoMfaInterno(1)).thenReturn(new EstadoMfaInternoDTO(false, null, false));
        when(jwtService.generateToken(any(), eq(1))).thenReturn("TOKEN-REAL");

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "clave"));

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData().getToken()).isEqualTo("TOKEN-REAL");
        assertThat(resultado.getData().isMfaRequerido()).isFalse();
        assertThat(resultado.getData().isMfaSetupRequerido()).isFalse();
    }

    @Test
    void loginConMfaHabilitadoSinConfirmarGeneraUnSecretoNuevoYPideConfigurar() {
        sinBloqueoPrevio();
        when(detailsService.loadUserByUsername("admin")).thenReturn(usuario(1, "admin"));
        when(mapper.obtenerEstadoMfaInterno(1)).thenReturn(new EstadoMfaInternoDTO(true, null, false));
        when(totpUtil.generarSecreto()).thenReturn("SECRETO-NUEVO");
        when(totpUtil.generarUriOtpAuth("SECRETO-NUEVO", "admin")).thenReturn("otpauth://totp/x");
        when(jwtService.generarTokenMfaPendiente("admin", 1)).thenReturn("PENDING-TOKEN");
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "clave"));

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData().isMfaSetupRequerido()).isTrue();
        assertThat(resultado.getData().getMfaToken()).isEqualTo("PENDING-TOKEN");
        assertThat(resultado.getData().getMfaSecretoManual()).isEqualTo("SECRETO-NUEVO");
        assertThat(resultado.getData().getToken()).isNull(); // el token real todavia no se entrega

        verify(mapper).activarMfa(1, "SECRETO-NUEVO", 1, "127.0.0.1");
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void loginConMfaYaConfirmadoNoRegeneraElSecretoYPideElCodigo() {
        sinBloqueoPrevio();
        when(detailsService.loadUserByUsername("admin")).thenReturn(usuario(1, "admin"));
        when(mapper.obtenerEstadoMfaInterno(1)).thenReturn(new EstadoMfaInternoDTO(true, "SECRETO-VIEJO", true));
        when(jwtService.generarTokenMfaPendiente("admin", 1)).thenReturn("PENDING-TOKEN-2");

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "clave"));

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData().isMfaRequerido()).isTrue();
        assertThat(resultado.getData().isMfaSetupRequerido()).isFalse();
        assertThat(resultado.getData().getMfaToken()).isEqualTo("PENDING-TOKEN-2");

        verify(mapper, never()).activarMfa(anyInt(), anyString(), anyInt(), anyString());
        verify(totpUtil, never()).generarSecreto();
    }

    @Test
    void loginConCredencialesIncorrectasNoRevelaSiFueElUsuarioOLaClave() {
        sinBloqueoPrevio();
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("mal"));

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "mala"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).isEqualTo("Usuario o contraseña incorrectos");
    }

    @Test
    void loginConCuentaInactivaAvisaQueEstaInactivaYNoEsUnErrorDeServidor() {
        sinBloqueoPrevio();
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("inactivo"));

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("cobranzas", "clave"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("inactivo");
    }

    // ------------------------------------------------------------------
    // Freno de fuerza bruta (LoginAttemptTracker)
    // ------------------------------------------------------------------

    @Test
    void rechazaElLoginSiLaIpEstaBloqueadaSinSiquieraValidarCredenciales() {
        when(audit.ipActual()).thenReturn("9.9.9.9");
        when(intentos.tiempoRestanteBloqueo("9.9.9.9")).thenReturn(Duration.ofMinutes(3));
        when(intentos.tiempoRestanteBloqueo("user:admin")).thenReturn(Duration.ZERO);

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "Admin123!"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("Demasiados intentos");
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void rechazaElLoginSiElUsuarioEstaBloqueadoAunquePruebeDesdeOtraIp() {
        when(audit.ipActual()).thenReturn("8.8.8.8"); // una IP nueva, nunca bloqueada
        when(intentos.tiempoRestanteBloqueo("8.8.8.8")).thenReturn(Duration.ZERO);
        when(intentos.tiempoRestanteBloqueo("user:admin")).thenReturn(Duration.ofMinutes(5));

        ResponseDTO<AuthDTO> resultado = servicio.login(new LoginRequest("admin", "Admin123!"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("Demasiados intentos");
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void unaContrasenaIncorrectaRegistraElFalloPorIpYPorUsuario() {
        sinBloqueoPrevio();
        when(audit.ipActual()).thenReturn("5.5.5.5");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("mal"));

        servicio.login(new LoginRequest("admin", "mala"));

        verify(intentos).registrarFallo("5.5.5.5");
        verify(intentos).registrarFallo("user:admin");
    }

    @Test
    void unLoginExitosoLimpiaElHistorialDeIntentosDeLaIpYDelUsuario() {
        sinBloqueoPrevio();
        when(audit.ipActual()).thenReturn("6.6.6.6");
        when(detailsService.loadUserByUsername("admin")).thenReturn(usuario(1, "admin"));
        when(mapper.obtenerEstadoMfaInterno(1)).thenReturn(new EstadoMfaInternoDTO(false, null, false));

        servicio.login(new LoginRequest("admin", "Admin123!"));

        verify(intentos).registrarExito("6.6.6.6");
        verify(intentos).registrarExito("user:admin");
    }

    // ------------------------------------------------------------------
    // Resguardo del ultimo Administrador activo (insertUpdateUsuario)
    // ------------------------------------------------------------------

    private UsuarioRequest edicion(Integer idUsuario, Integer idRol, String estado) {
        UsuarioRequest req = new UsuarioRequest();
        req.setIdUsuario(idUsuario);
        req.setIdRol(idRol);
        req.setEstado(estado);
        return req;
    }

    @Test
    void noPermiteDesactivarAlUnicoAdministradorActivo() {
        // estado="INACTIVO" ya hace que "quedaraAdministradorActivo" sea
        // false por cortocircuito de &&, sin llegar a consultar el rol.
        when(mapper.contarAdministradoresActivosDistintosDe(1)).thenReturn(0);

        ResponseDTO<Integer> resultado = servicio.insertUpdateUsuario(edicion(1, 1, "INACTIVO"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("único usuario con rol Administrador");
        verify(mapper, never()).actualizarUsuario(any(), any(), any());
    }

    @Test
    void noPermiteQuitarleElRolDeAdministradorAlUnicoActivo() {
        when(mapper.esRolAdministrador(2)).thenReturn(false); // se lo cambian a otro rol
        when(mapper.contarAdministradoresActivosDistintosDe(1)).thenReturn(0);

        ResponseDTO<Integer> resultado = servicio.insertUpdateUsuario(edicion(1, 2, "ACTIVO"));

        assertThat(resultado.isError()).isTrue();
        verify(mapper, never()).actualizarUsuario(any(), any(), any());
    }

    @Test
    void permiteElCambioSiQuedaOtroAdministradorActivo() {
        when(mapper.contarAdministradoresActivosDistintosDe(1)).thenReturn(2);

        ResponseDTO<Integer> resultado = servicio.insertUpdateUsuario(edicion(1, 1, "INACTIVO"));

        assertThat(resultado.isError()).isFalse();
        verify(mapper).actualizarUsuario(any(), any(), any());
    }

    @Test
    void permiteElCambioQueMantieneAlUsuarioComoAdministradorActivoSinConsultarElConteo() {
        when(mapper.esRolAdministrador(1)).thenReturn(true);

        ResponseDTO<Integer> resultado = servicio.insertUpdateUsuario(edicion(1, 1, "ACTIVO"));

        assertThat(resultado.isError()).isFalse();
        // cortocircuito: si va a seguir siendo Administrador activo, ni
        // siquiera hace falta contar a los demas.
        verify(mapper, never()).contarAdministradoresActivosDistintosDe(anyInt());
        verify(mapper).actualizarUsuario(any(), any(), any());
    }

    // ------------------------------------------------------------------
    // Resguardo del ultimo Administrador activo (eliminarUsuario)
    // ------------------------------------------------------------------

    @Test
    void noPermiteEliminarseAUnoMismoConLaSesionIniciada() {
        when(audit.usuarioActual()).thenReturn(5);

        ResponseDTO<Integer> resultado = servicio.eliminarUsuario(edicion(5, 1, "ACTIVO"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("propio usuario");
        verifyNoInteractions(mapper);
    }

    @Test
    void noPermiteEliminarAlUnicoAdministradorActivo() {
        when(audit.usuarioActual()).thenReturn(5); // otro usuario distinto al que se quiere borrar
        when(mapper.esAdministradorActivo(1)).thenReturn(true);
        when(mapper.contarAdministradoresActivosDistintosDe(1)).thenReturn(0);

        ResponseDTO<Integer> resultado = servicio.eliminarUsuario(edicion(1, 1, "ACTIVO"));

        assertThat(resultado.isError()).isTrue();
        verify(mapper, never()).eliminarUsuario(anyInt(), any(), any());
    }

    @Test
    void permiteEliminarUnUsuarioQueNoEsAdministrador() {
        when(audit.usuarioActual()).thenReturn(5);
        when(mapper.esAdministradorActivo(2)).thenReturn(false);
        when(mapper.eliminarUsuario(eq(2), any(), any())).thenReturn(1);

        ResponseDTO<Integer> resultado = servicio.eliminarUsuario(edicion(2, 2, "ACTIVO"));

        assertThat(resultado.isError()).isFalse();
        verify(mapper, never()).contarAdministradoresActivosDistintosDe(anyInt());
    }

    // ------------------------------------------------------------------
    // Cambio de la propia contraseña
    // ------------------------------------------------------------------

    @Test
    void rechazaElCambioDeMiPasswordSiLaActualNoCoincide() {
        when(audit.usuarioActual()).thenReturn(1);
        when(mapper.obtenerPasswordHash(1)).thenReturn("HASH-REAL");
        when(passwordEncoder.matches("incorrecta", "HASH-REAL")).thenReturn(false);

        CambiarMiPasswordRequest req = new CambiarMiPasswordRequest();
        req.setPasswordActual("incorrecta");
        req.setPasswordNueva("claveNueva123");

        ResponseDTO<Integer> resultado = servicio.cambiarMiPassword(req);

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("no es correcta");
        verify(mapper, never()).actualizarPassword(anyInt(), anyString(), anyInt(), anyString());
    }

    @Test
    void cambiaMiPasswordCuandoLaActualEsCorrecta() {
        when(audit.usuarioActual()).thenReturn(1);
        when(mapper.obtenerPasswordHash(1)).thenReturn("HASH-REAL");
        when(passwordEncoder.matches("correcta", "HASH-REAL")).thenReturn(true);
        when(passwordEncoder.encode("claveNueva123")).thenReturn("HASH-NUEVO");
        when(audit.ipActual()).thenReturn("127.0.0.1");

        CambiarMiPasswordRequest req = new CambiarMiPasswordRequest();
        req.setPasswordActual("correcta");
        req.setPasswordNueva("claveNueva123");

        ResponseDTO<Integer> resultado = servicio.cambiarMiPassword(req);

        assertThat(resultado.isError()).isFalse();
        verify(mapper).actualizarPassword(1, "HASH-NUEVO", 1, "127.0.0.1");
    }

    // ------------------------------------------------------------------
    // El administrador reinicia el MFA de otro usuario (telefono perdido)
    // ------------------------------------------------------------------

    @Test
    void elAdministradorReiniciaElMfaDeOtroUsuarioSinPedirleUnCodigo() {
        when(mapper.resetearMfa(2, 1, "127.0.0.1")).thenReturn(1);
        when(audit.usuarioActual()).thenReturn(1);
        when(audit.ipActual()).thenReturn("127.0.0.1");

        ResponseDTO<Integer> resultado = servicio.resetearMfaDeUsuario(edicion(2, 3, "ACTIVO"));

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getMensaje()).contains("próximo inicio de sesión");
        verify(mapper).resetearMfa(2, 1, "127.0.0.1");
        // a diferencia del autoservicio, esto nunca valida un codigo TOTP:
        // seria imposible pedirselo a alguien que perdio el telefono.
        verify(totpUtil, never()).validarCodigo(anyString(), anyString());
    }

    @Test
    void rechazaReiniciarElMfaSinIndicarUnUsuario() {
        ResponseDTO<Integer> resultado = servicio.resetearMfaDeUsuario(edicion(null, 3, "ACTIVO"));

        assertThat(resultado.isError()).isTrue();
        verifyNoInteractions(mapper);
    }

    @Test
    void devuelveErrorSiElUsuarioAReiniciarNoExiste() {
        when(mapper.resetearMfa(eq(99), any(), any())).thenReturn(0);

        ResponseDTO<Integer> resultado = servicio.resetearMfaDeUsuario(edicion(99, 3, "ACTIVO"));

        assertThat(resultado.isError()).isTrue();
        assertThat(resultado.getMensaje()).contains("No se encontró");
    }

    // ------------------------------------------------------------------
    // Permisos del usuario de la sesion (menu y rutas dinamicas)
    // ------------------------------------------------------------------

    @Test
    void obtieneLosPermisosDelUsuarioDeLaSesionSegunSuRol() {
        when(audit.usuarioActual()).thenReturn(1);
        when(mapper.listarPermisosDeUsuario(1)).thenReturn(java.util.List.of(
                new com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO("UNIDADES", "ESCRITURA"),
                new com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO("MANTENIMIENTO", "LECTURA")
        ));

        ResponseDTO<java.util.List<com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO>> resultado = servicio.obtenerMisPermisos();

        assertThat(resultado.isError()).isFalse();
        assertThat(resultado.getData()).extracting(com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO::getCodigo)
                .containsExactly("UNIDADES", "MANTENIMIENTO");
    }

    @Test
    void noPuedeObtenerPermisosSiNoHayUsuarioEnLaSesion() {
        when(audit.usuarioActual()).thenReturn(null);

        ResponseDTO<java.util.List<com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO>> resultado = servicio.obtenerMisPermisos();

        assertThat(resultado.isError()).isTrue();
        verifyNoInteractions(mapper);
    }
}

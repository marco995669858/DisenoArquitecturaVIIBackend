package com.dataverse.rentals.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperUsuario;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.usuario.request.CambiarMiPasswordRequest;
import com.dataverse.rentals.model.usuario.request.CambiarPasswordRequest;
import com.dataverse.rentals.model.usuario.request.CodigoMfaRequest;
import com.dataverse.rentals.model.usuario.request.LoginRequest;
import com.dataverse.rentals.model.usuario.request.UsuarioRequest;
import com.dataverse.rentals.model.usuario.request.VerificarMfaRequest;
import com.dataverse.rentals.model.usuario.response.AuthDTO;
import com.dataverse.rentals.model.usuario.response.EstadoMfaDTO;
import com.dataverse.rentals.model.usuario.response.EstadoMfaInternoDTO;
import com.dataverse.rentals.model.usuario.response.MfaSetupDTO;
import com.dataverse.rentals.model.usuario.response.RefreshResponse;
import com.dataverse.rentals.model.usuario.response.RefreshTokenDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.security.CustomUserDetails;
import com.dataverse.rentals.security.CustomUserDetailsService;
import com.dataverse.rentals.security.JwtService;
import com.dataverse.rentals.security.LoginAttemptTracker;
import com.dataverse.rentals.security.TotpUtil;
import com.dataverse.rentals.service.ServiceUsuario;
import com.dataverse.rentals.util.ErrorMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplUsuario implements ServiceUsuario {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService detailsService;
    private final MapperUsuario mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditContext audit;
    private final TotpUtil totpUtil;
    private final LoginAttemptTracker intentos;

    // El Refresh Token durará 8 horas (en milisegundos)
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 28800000L;

    @Override
    public ResponseDTO<AuthDTO> login(LoginRequest args) {
        String claveIp = audit.ipActual();
        String claveUsuario = "user:" + args.username().toLowerCase();

        Duration bloqueo = mayor(intentos.tiempoRestanteBloqueo(claveIp), intentos.tiempoRestanteBloqueo(claveUsuario));
        if (!bloqueo.isZero()) {
            long minutos = Math.max(1, bloqueo.toMinutes());
            log.warn("Login bloqueado por demasiados intentos fallidos: usuario={}, ip={}", args.username(), claveIp);
            return new ResponseDTO<>(null, true, "Demasiados intentos fallidos. Intente nuevamente en " + minutos + " minuto(s).");
        }

        try {
            // 1. Spring valida credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(args.username(), args.password())
            );

            // Contraseña correcta: se limpia el historial de fallos de esta
            // IP y de este usuario (ya no tiene sentido seguir contandolos).
            intentos.registrarExito(claveIp);
            intentos.registrarExito(claveUsuario);

            // 2. Traemos la data completa del usuario
            CustomUserDetails user = (CustomUserDetails) detailsService.loadUserByUsername(args.username());
            Integer idUsuario = user.getIdUsuario();

            // 3. MFA es obligatorio por defecto para todos (mfa_habilitado=1 al
            // crear el usuario); cada quien puede desactivarlo despues desde su
            // perfil. Si esta habilitado, todavia no se entrega el token real.
            EstadoMfaInternoDTO estadoMfa = mapper.obtenerEstadoMfaInterno(idUsuario);
            boolean mfaHabilitado = estadoMfa != null && Boolean.TRUE.equals(estadoMfa.getMfaHabilitado());

            if (!mfaHabilitado) {
                return emitirTokensExitosos(user, idUsuario);
            }

            boolean yaConfirmado = estadoMfa != null && Boolean.TRUE.equals(estadoMfa.getMfaConfirmado());
            String mfaToken = jwtService.generarTokenMfaPendiente(args.username(), idUsuario);

            if (!yaConfirmado) {
                // Primera vez, o quedo a medias (nunca llego a confirmar un
                // codigo): se genera un secreto nuevo y se muestra el QR de
                // nuevo, para que nadie quede pidiendo un codigo de un
                // secreto que nunca alcanzo a guardar en su app.
                String secreto = totpUtil.generarSecreto();
                mapper.activarMfa(idUsuario, secreto, idUsuario, audit.ipActual());
                String uri = totpUtil.generarUriOtpAuth(secreto, args.username());

                AuthDTO data = AuthDTO.builder()
                        .mfaSetupRequerido(true)
                        .mfaToken(mfaToken)
                        .mfaSecretoManual(secreto)
                        .mfaOtpAuthUri(uri)
                        .build();
                return new ResponseDTO<>(data, false, "Configure la verificación en dos pasos para continuar.");
            }

            AuthDTO data = AuthDTO.builder().mfaRequerido(true).mfaToken(mfaToken).build();
            return new ResponseDTO<>(data, false, "Ingrese el código de verificación en dos pasos.");

        } catch (BadCredentialsException e) {
            intentos.registrarFallo(claveIp);
            intentos.registrarFallo(claveUsuario);
            log.warn("Intento de login fallido para el usuario: {}", args.username());
            // Mensaje amigable, nunca revelamos si fue el usuario o la clave lo que falló
            return new ResponseDTO<>(null, true, "Usuario o contraseña incorrectos");

        } catch (AccountStatusException e) {
            // DisabledException (estado INACTIVO) u otras cuentas no habilitadas: es un
            // caso esperado del negocio, no un fallo del sistema -- no va como log.error.
            log.warn("Intento de login de una cuenta no habilitada: {}", args.username());
            return new ResponseDTO<>(null, true, "El usuario está inactivo. Contacte al administrador.");

        } catch (AuthenticationException e) {
            log.error("Error inesperado durante el login del usuario {}: {}", args.username(), e.getMessage(), e);
            return new ResponseDTO<>(null, true, "Error interno del servidor. Por favor, intente más tarde.");
        }
    }

    @Override
    public RefreshResponse refrescarToken(String refreshToken) {
        RefreshTokenDTO tokenGuardado = mapper.obtenerTokenUsuario(refreshToken);

        if (tokenGuardado == null) {
            throw new RuntimeException("Refresh token inválido.");
        }
        if (tokenGuardado.getFechaExpiracion().isBefore(Instant.now())) {
            mapper.eliminarToken(refreshToken);
            throw new RuntimeException("El Refresh Token ha expirado. Por favor, inicie sesión nuevamente.");
        }

        // Rotamos: invalidamos el token usado y emitimos uno nuevo junto al access token
        mapper.eliminarToken(refreshToken);

        CustomUserDetails user = (CustomUserDetails) detailsService.loadUserByUsername(tokenGuardado.getUsername());
        String nuevoAccessToken = jwtService.generateToken(user, user.getIdUsuario());
        String nuevoRefreshToken = crearRefreshToken(user.getIdUsuario());

        return new RefreshResponse(nuevoAccessToken, nuevoRefreshToken);
    }

    private ResponseDTO<AuthDTO> emitirTokensExitosos(CustomUserDetails user, Integer idUsuario) {
        String token = jwtService.generateToken(user, idUsuario);
        String refreshToken = crearRefreshToken(idUsuario);
        AuthDTO data = AuthDTO.builder().token(token).refreshToken(refreshToken).build();
        return new ResponseDTO<>(data, false, "Login exitoso");
    }

    @Override
    public ResponseDTO<AuthDTO> verificarMfa(VerificarMfaRequest args) {
        try {
            String username = jwtService.validarTokenMfaPendiente(args.getMfaToken());
            if (username == null) {
                return new ResponseDTO<>(true, "El proceso de verificación expiró. Inicie sesión nuevamente.");
            }

            CustomUserDetails user = (CustomUserDetails) detailsService.loadUserByUsername(username);
            Integer idUsuario = user.getIdUsuario();

            EstadoMfaInternoDTO estado = mapper.obtenerEstadoMfaInterno(idUsuario);
            if (estado == null || estado.getMfaSecret() == null) {
                return new ResponseDTO<>(true, "No hay una verificación en dos pasos pendiente para este usuario.");
            }
            if (!totpUtil.validarCodigo(estado.getMfaSecret(), args.getCodigo())) {
                return new ResponseDTO<>(true, "El código ingresado no es correcto.");
            }

            if (!Boolean.TRUE.equals(estado.getMfaConfirmado())) {
                mapper.confirmarMfa(idUsuario, idUsuario, audit.ipActual());
            }

            return emitirTokensExitosos(user, idUsuario);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<EstadoMfaDTO> obtenerEstadoMiMfa() {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            EstadoMfaInternoDTO estado = mapper.obtenerEstadoMfaInterno(idUsuario);
            EstadoMfaDTO data = new EstadoMfaDTO(
                    estado != null && Boolean.TRUE.equals(estado.getMfaHabilitado()),
                    estado != null && Boolean.TRUE.equals(estado.getMfaConfirmado())
            );
            return new ResponseDTO<>(data, false, "OK");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<MfaSetupDTO> activarMiMfa() {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            String username = mapper.obtenerNombreUsuarioPorId(idUsuario);
            String secreto = totpUtil.generarSecreto();
            mapper.activarMfa(idUsuario, secreto, idUsuario, audit.ipActual());
            String uri = totpUtil.generarUriOtpAuth(secreto, username);
            return new ResponseDTO<>(new MfaSetupDTO(secreto, uri), false,
                    "Escanee el código QR con su app de autenticación y confirme con el código generado.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> confirmarMiMfa(CodigoMfaRequest args) {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            EstadoMfaInternoDTO estado = mapper.obtenerEstadoMfaInterno(idUsuario);
            if (estado == null || estado.getMfaSecret() == null) {
                return new ResponseDTO<>(true, "Primero debe iniciar la activación de la verificación en dos pasos.");
            }
            if (!totpUtil.validarCodigo(estado.getMfaSecret(), args.getCodigo())) {
                return new ResponseDTO<>(true, "El código ingresado no es correcto.");
            }
            mapper.confirmarMfa(idUsuario, idUsuario, audit.ipActual());
            return new ResponseDTO<>(idUsuario, false, "Verificación en dos pasos activada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> desactivarMiMfa(CodigoMfaRequest args) {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            EstadoMfaInternoDTO estado = mapper.obtenerEstadoMfaInterno(idUsuario);
            if (estado == null || !Boolean.TRUE.equals(estado.getMfaHabilitado())) {
                return new ResponseDTO<>(true, "La verificación en dos pasos ya está desactivada.");
            }
            if (!totpUtil.validarCodigo(estado.getMfaSecret(), args.getCodigo())) {
                return new ResponseDTO<>(true, "El código ingresado no es correcto.");
            }
            mapper.desactivarMfa(idUsuario, idUsuario, audit.ipActual());
            return new ResponseDTO<>(idUsuario, false, "Verificación en dos pasos desactivada.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> resetearMfaDeUsuario(UsuarioRequest args) {
        try {
            if (args.getIdUsuario() == null) {
                return new ResponseDTO<>(true, "Debe indicar el usuario.");
            }
            int filas = mapper.resetearMfa(args.getIdUsuario(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el usuario indicado.");
            }
            return new ResponseDTO<>(args.getIdUsuario(), false,
                    "Verificación en dos pasos reiniciada. El usuario deberá configurarla de nuevo en su próximo inicio de sesión.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<List<PermisoAsignadoDTO>> obtenerMisPermisos() {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            return new ResponseDTO<>(mapper.listarPermisosDeUsuario(idUsuario), false, "OK");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    private Duration mayor(Duration a, Duration b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private String crearRefreshToken(Integer idUsuario) {
        String token = UUID.randomUUID().toString();
        RefreshTokenDTO refreshToken = new RefreshTokenDTO(
                null, idUsuario, null, token, Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS));
        mapper.insertarToken(refreshToken);
        return token;
    }

    @Override
    public ResponseDTO<List<UsuarioDTO>> listarUsuarios() {
        try {
            List<UsuarioDTO> data = mapper.listarUsuarios();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> insertUpdateUsuario(UsuarioRequest args) {
        try {
            if (args.getIdRol() == null) {
                return new ResponseDTO<>(true, "Debe indicar el rol del usuario.");
            }

            Integer idAuditoria = audit.usuarioActual();
            String ip = audit.ipActual();

            if (args.getIdUsuario() == null) {
                // nombreUsuario y password solo son obligatorios al crear: en la
                // edicion ninguno de los dos se actualiza (ver actualizarUsuario).
                if (args.getNombreUsuario() == null || args.getNombreUsuario().isBlank()) {
                    return new ResponseDTO<>(true, "Debe indicar el nombre de usuario.");
                }
                if (args.getPassword() == null || args.getPassword().length() < 8) {
                    return new ResponseDTO<>(true, "La contraseña del nuevo usuario debe tener al menos 8 caracteres.");
                }
                if (args.getEstado() == null || args.getEstado().isBlank()) {
                    args.setEstado("ACTIVO");
                }
                String hash = passwordEncoder.encode(args.getPassword());
                mapper.insertarUsuario(args, hash, idAuditoria, ip);
                return new ResponseDTO<>(args.getIdUsuario(), false, "Usuario registrado correctamente.");
            }

            // El sistema no puede quedarse sin ningun Administrador activo: si
            // este cambio va a dejar al usuario sin ese rol o sin estado
            // ACTIVO, y no queda ningun otro Administrador activo, se bloquea
            // -- sin importar si lo esta haciendo otra persona o el mismo
            // usuario sobre su propia cuenta.
            boolean quedaraAdministradorActivo = "ACTIVO".equals(args.getEstado()) && mapper.esRolAdministrador(args.getIdRol());
            if (!quedaraAdministradorActivo && mapper.contarAdministradoresActivosDistintosDe(args.getIdUsuario()) == 0) {
                return new ResponseDTO<>(true, "No se puede aplicar el cambio: es el único usuario con rol Administrador y estado Activo. Active o cree otro Administrador antes de continuar.");
            }

            mapper.actualizarUsuario(args, idAuditoria, ip);
            return new ResponseDTO<>(args.getIdUsuario(), false, "Usuario actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> cambiarPassword(CambiarPasswordRequest args) {
        try {
            if (args.getIdUsuario() == null || args.getPasswordNueva() == null || args.getPasswordNueva().length() < 8) {
                return new ResponseDTO<>(true, "Debe indicar el usuario y una contraseña de al menos 8 caracteres.");
            }
            String hash = passwordEncoder.encode(args.getPasswordNueva());
            int filas = mapper.actualizarPassword(args.getIdUsuario(), hash, audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el usuario indicado.");
            }
            return new ResponseDTO<>(filas, false, "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> cambiarMiPassword(CambiarMiPasswordRequest args) {
        try {
            Integer idUsuario = audit.usuarioActual();
            if (idUsuario == null) {
                return new ResponseDTO<>(true, "No se pudo identificar al usuario de la sesión.");
            }
            if (args.getPasswordNueva() == null || args.getPasswordNueva().length() < 8) {
                return new ResponseDTO<>(true, "La contraseña nueva debe tener al menos 8 caracteres.");
            }

            String hashActual = mapper.obtenerPasswordHash(idUsuario);
            if (hashActual == null) {
                return new ResponseDTO<>(true, "No se encontró el usuario indicado.");
            }
            if (args.getPasswordActual() == null || !passwordEncoder.matches(args.getPasswordActual(), hashActual)) {
                return new ResponseDTO<>(true, "La contraseña actual no es correcta.");
            }

            String hashNuevo = passwordEncoder.encode(args.getPasswordNueva());
            mapper.actualizarPassword(idUsuario, hashNuevo, idUsuario, audit.ipActual());
            return new ResponseDTO<>(idUsuario, false, "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarUsuario(UsuarioRequest args) {
        try {
            Integer idAuditoria = audit.usuarioActual();
            if (idAuditoria != null && idAuditoria.equals(args.getIdUsuario())) {
                return new ResponseDTO<>(true, "No puede eliminar su propio usuario mientras tiene la sesión iniciada.");
            }
            if (mapper.esAdministradorActivo(args.getIdUsuario()) && mapper.contarAdministradoresActivosDistintosDe(args.getIdUsuario()) == 0) {
                return new ResponseDTO<>(true, "No se puede eliminar: es el único usuario con rol Administrador y estado Activo.");
            }
            int filas = mapper.eliminarUsuario(args.getIdUsuario(), idAuditoria, audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el usuario a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Usuario eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

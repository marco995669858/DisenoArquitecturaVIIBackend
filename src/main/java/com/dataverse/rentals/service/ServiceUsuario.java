package com.dataverse.rentals.service;

import java.util.List;

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
import com.dataverse.rentals.model.usuario.response.MfaSetupDTO;
import com.dataverse.rentals.model.usuario.response.RefreshResponse;
import com.dataverse.rentals.model.usuario.response.UsuarioDTO;

public interface ServiceUsuario {

    ResponseDTO<AuthDTO> login(LoginRequest args);

    RefreshResponse refrescarToken(String refreshToken);

    ResponseDTO<List<UsuarioDTO>> listarUsuarios();

    ResponseDTO<Integer> insertUpdateUsuario(UsuarioRequest args);

    ResponseDTO<Integer> cambiarPassword(CambiarPasswordRequest args);

    /** Cualquier usuario autenticado cambia su PROPIA contraseña; exige la actual (a diferencia de cambiarPassword, uso exclusivo del administrador). */
    ResponseDTO<Integer> cambiarMiPassword(CambiarMiPasswordRequest args);

    ResponseDTO<Integer> eliminarUsuario(UsuarioRequest args);

    /** Segundo paso del login cuando el usuario tiene MFA activo (o lo esta activando por primera vez). */
    ResponseDTO<AuthDTO> verificarMfa(VerificarMfaRequest args);

    /** Estado de MFA del usuario de la sesión actual (para la pantalla de "Seguridad" del perfil). */
    ResponseDTO<EstadoMfaDTO> obtenerEstadoMiMfa();

    /** El propio usuario activa su MFA: genera un secreto nuevo y devuelve los datos para el QR. */
    ResponseDTO<MfaSetupDTO> activarMiMfa();

    /** Confirma la activacion con el primer código generado por la app de autenticación. */
    ResponseDTO<Integer> confirmarMiMfa(CodigoMfaRequest args);

    /** El propio usuario desactiva su MFA (exige un código válido como prueba de que sigue teniendo acceso). */
    ResponseDTO<Integer> desactivarMiMfa(CodigoMfaRequest args);

    /** El administrador reinicia el MFA de OTRO usuario (p.ej. perdió el teléfono): no pide código, y en su próximo login se le pedirá configurarlo de nuevo. */
    ResponseDTO<Integer> resetearMfaDeUsuario(UsuarioRequest args);

    /** Codigos de modulo que el usuario de la sesión actual puede ver, segun los permisos de su rol. */
    ResponseDTO<List<PermisoAsignadoDTO>> obtenerMisPermisos();
}

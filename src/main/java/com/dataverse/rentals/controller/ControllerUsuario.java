package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.usuario.request.CambiarMiPasswordRequest;
import com.dataverse.rentals.model.usuario.request.CambiarPasswordRequest;
import com.dataverse.rentals.model.usuario.request.CodigoMfaRequest;
import com.dataverse.rentals.model.usuario.request.UsuarioRequest;
import com.dataverse.rentals.model.usuario.response.EstadoMfaDTO;
import com.dataverse.rentals.model.usuario.response.MfaSetupDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioDTO;
import com.dataverse.rentals.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

/** CRUD de usuarios del sistema. Login y refresh de token viven en ControllerAuth. */
@RestController
@RequestMapping("${dataverse.rentals.controller.path}usuario")
@RequiredArgsConstructor
public class ControllerUsuario {

    private final ServiceUsuario service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<UsuarioDTO>> listarUsuarios() {
        return service.listarUsuarios();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateUsuario(@RequestBody UsuarioRequest args) {
        return service.insertUpdateUsuario(args);
    }

    @PostMapping(value = "/cambiar-password")
    public ResponseDTO<Integer> cambiarPassword(@RequestBody CambiarPasswordRequest args) {
        return service.cambiarPassword(args);
    }

    /** Cualquier usuario autenticado -- no solo el administrador -- cambia su propia contraseña, con la actual como requisito. */
    @PostMapping(value = "/cambiar-mi-password")
    public ResponseDTO<Integer> cambiarMiPassword(@RequestBody CambiarMiPasswordRequest args) {
        return service.cambiarMiPassword(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarUsuario(@RequestBody UsuarioRequest args) {
        return service.eliminarUsuario(args);
    }

    // ------------------------------------------------------------------
    // MFA -- cada usuario administra la suya propia desde su perfil.
    // ------------------------------------------------------------------

    @GetMapping(value = "/mfa/estado")
    public ResponseDTO<EstadoMfaDTO> obtenerEstadoMiMfa() {
        return service.obtenerEstadoMiMfa();
    }

    @PostMapping(value = "/mfa/activar")
    public ResponseDTO<MfaSetupDTO> activarMiMfa() {
        return service.activarMiMfa();
    }

    @PostMapping(value = "/mfa/confirmar")
    public ResponseDTO<Integer> confirmarMiMfa(@RequestBody CodigoMfaRequest args) {
        return service.confirmarMiMfa(args);
    }

    @PostMapping(value = "/mfa/desactivar")
    public ResponseDTO<Integer> desactivarMiMfa(@RequestBody CodigoMfaRequest args) {
        return service.desactivarMiMfa(args);
    }

    /** Solo para el administrador: reinicia el MFA de otro usuario (p.ej. perdió el teléfono). */
    @PostMapping(value = "/mfa/resetear")
    public ResponseDTO<Integer> resetearMfaDeUsuario(@RequestBody UsuarioRequest args) {
        return service.resetearMfaDeUsuario(args);
    }

    /** Modulos que el usuario de la sesión actual puede ver, segun los permisos de su rol (para el menu y las rutas). */
    @GetMapping(value = "/mis-permisos")
    public ResponseDTO<List<PermisoAsignadoDTO>> obtenerMisPermisos() {
        return service.obtenerMisPermisos();
    }
}

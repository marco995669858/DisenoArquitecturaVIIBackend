package com.dataverse.rentals.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.usuario.request.LoginRequest;
import com.dataverse.rentals.model.usuario.request.RefreshRequest;
import com.dataverse.rentals.model.usuario.request.VerificarMfaRequest;
import com.dataverse.rentals.model.usuario.response.AuthDTO;
import com.dataverse.rentals.model.usuario.response.RefreshResponse;
import com.dataverse.rentals.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}auth")
@RequiredArgsConstructor
public class ControllerAuth {

    private final ServiceUsuario service;

    @PostMapping(value = "/login")
    public ResponseDTO<AuthDTO> login(@RequestBody LoginRequest args) {
        return service.login(args);
    }

    @PostMapping(value = "/refresh")
    public RefreshResponse refresh(@RequestBody RefreshRequest args) {
        return service.refrescarToken(args.token());
    }

    /** Segundo paso del login cuando /auth/login devolvió mfaRequerido o mfaSetupRequerido. */
    @PostMapping(value = "/mfa/verificar")
    public ResponseDTO<AuthDTO> verificarMfa(@RequestBody VerificarMfaRequest args) {
        return service.verificarMfa(args);
    }
}

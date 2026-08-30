package com.pe.consultorio.clinico.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.usuario.request.LoginRequest;
import com.pe.consultorio.clinico.model.usuario.response.AuthDTO;
import com.pe.consultorio.clinico.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${consultorio.clinico.controller.path}auth")
@RequiredArgsConstructor
public class ControllerAuth {
    
    private final ServiceUsuario service;
    
    @PostMapping(value="/login")
    public ResponseDTO<AuthDTO> insertUpdateEspecialidad(@RequestBody LoginRequest args){
        return service.login(args);
    }
}

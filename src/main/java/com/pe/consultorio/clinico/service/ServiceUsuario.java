package com.pe.consultorio.clinico.service;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.usuario.request.LoginRequest;
import com.pe.consultorio.clinico.model.usuario.response.AuthDTO;

public interface ServiceUsuario {
    
    ResponseDTO<AuthDTO> login(LoginRequest args); 
}

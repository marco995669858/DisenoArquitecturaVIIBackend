package com.pe.consultorio.clinico.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.especialidad.request.EspecialidadRequest;
import com.pe.consultorio.clinico.model.especialidad.response.EspecialidadDTO;
import com.pe.consultorio.clinico.service.ServiceEspecialidad;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${consultorio.clinico.controller.path}especialidad")
@RequiredArgsConstructor
public class ControllerEspecialidad {

    private final ServiceEspecialidad service;

    @GetMapping(value="/listar")
    public ResponseDTO<List<EspecialidadDTO>> listarEspecialidades(){
        return service.listarEspecialidades();
    }

    @PostMapping(value="/insertar/actulizar")
    public ResponseDTO<Integer> insertUpdateEspecialidad(@RequestBody EspecialidadRequest args){
        return service.insertUpdateEspecialidad(args);
    }

    @PostMapping(value="/eliminar")
    public ResponseDTO<Integer> eliminarEspecialidad(@RequestBody EspecialidadRequest args){
        return service.eliminarEspecialidad(args);
    }
}

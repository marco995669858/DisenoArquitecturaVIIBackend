package com.pe.consultorio.clinico.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.estadoCita.request.EstadoCitaRequest;
import com.pe.consultorio.clinico.model.estadoCita.response.EstadoCitaDTO;
import com.pe.consultorio.clinico.service.ServiceEstadoCita;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${consultorio.clinico.controller.path}estado/cita")
@RequiredArgsConstructor
public class ControllerEstadoCita {

    private final ServiceEstadoCita service;

    @GetMapping(value="/listar")
    public ResponseDTO<List<EstadoCitaDTO>> listarEstadoCitas(){
        return service.listarEstadoCitas();
    }

    @PostMapping(value="/insertar/actulizar")
    public ResponseDTO<Integer> insertUpdateEstadoCita(@RequestBody EstadoCitaRequest args){
        return service.insertUpdateEstadoCita(args);
    }

    @PostMapping(value="/eliminar")
    public ResponseDTO<Integer> eliminarEstadoCita(@RequestBody EstadoCitaRequest args){
        return service.eliminarEstadoCita(args);
    }
}

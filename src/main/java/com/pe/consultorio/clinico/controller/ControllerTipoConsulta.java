package com.pe.consultorio.clinico.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.tipoConsullta.request.TipoConsultaRequest;
import com.pe.consultorio.clinico.model.tipoConsullta.response.TipoConsultaDTO;
import com.pe.consultorio.clinico.service.ServiceTipoConsulta;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${consultorio.clinico.controller.path}tipo/consulta")
@RequiredArgsConstructor
public class ControllerTipoConsulta {

    private final ServiceTipoConsulta service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<TipoConsultaDTO>> listarTipoConsultas() {
        return service.listarTipoConsultas();
    }

    @PostMapping(value = "/insertar/actulizar")
    public ResponseDTO<Integer> insertUpdateTipoConsulta(@RequestBody TipoConsultaRequest args) {
        return service.insertUpdateTipoConsulta(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarTipoConsulta(@RequestBody TipoConsultaRequest args) {
        return service.eliminarTipoConsulta(args);
    }
}

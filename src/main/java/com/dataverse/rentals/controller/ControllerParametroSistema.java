package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.parametroSistema.request.ParametroSistemaRequest;
import com.dataverse.rentals.model.parametroSistema.response.ParametroSistemaDTO;
import com.dataverse.rentals.service.ServiceParametroSistema;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}parametro-sistema")
@RequiredArgsConstructor
public class ControllerParametroSistema {

    private final ServiceParametroSistema service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<ParametroSistemaDTO>> listarParametros() {
        return service.listarParametros();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateParametro(@RequestBody ParametroSistemaRequest args) {
        return service.insertUpdateParametro(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarParametro(@RequestBody ParametroSistemaRequest args) {
        return service.eliminarParametro(args);
    }
}

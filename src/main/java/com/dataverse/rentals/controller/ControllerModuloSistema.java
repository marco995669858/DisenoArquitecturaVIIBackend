package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.ModuloSistemaDTO;
import com.dataverse.rentals.service.ServiceModuloSistema;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}modulo")
@RequiredArgsConstructor
public class ControllerModuloSistema {

    private final ServiceModuloSistema service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<ModuloSistemaDTO>> listarModulos() {
        return service.listarModulos();
    }
}

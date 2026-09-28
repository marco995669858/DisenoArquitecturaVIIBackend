package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.unidad.request.UnidadRequest;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;
import com.dataverse.rentals.service.ServiceUnidad;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}unidad")
@RequiredArgsConstructor
public class ControllerUnidad {

    private final ServiceUnidad service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<UnidadDTO>> listarUnidades() {
        return service.listarUnidades();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateUnidad(@RequestBody UnidadRequest args) {
        return service.insertUpdateUnidad(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarUnidad(@RequestBody UnidadRequest args) {
        return service.eliminarUnidad(args);
    }
}

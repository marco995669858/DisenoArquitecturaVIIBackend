package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.mantenimiento.request.MantenimientoRequest;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;
import com.dataverse.rentals.service.ServiceMantenimiento;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}mantenimiento")
@RequiredArgsConstructor
public class ControllerMantenimiento {

    private final ServiceMantenimiento service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<MantenimientoDTO>> listarMantenimientos() {
        return service.listarMantenimientos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateMantenimiento(@RequestBody MantenimientoRequest args) {
        return service.insertUpdateMantenimiento(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarMantenimiento(@RequestBody MantenimientoRequest args) {
        return service.eliminarMantenimiento(args);
    }
}

package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.prorrateo.request.CalcularProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.request.ProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.response.ProrrateoDTO;
import com.dataverse.rentals.service.ServiceProrrateo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}prorrateo")
@RequiredArgsConstructor
public class ControllerProrrateo {

    private final ServiceProrrateo service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<ProrrateoDTO>> listarProrrateos() {
        return service.listarProrrateos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateProrrateo(@RequestBody ProrrateoRequest args) {
        return service.insertUpdateProrrateo(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarProrrateo(@RequestBody ProrrateoRequest args) {
        return service.eliminarProrrateo(args);
    }

    @PostMapping(value = "/calcular")
    public ResponseDTO<List<ProrrateoDTO>> calcularProrrateo(@RequestBody CalcularProrrateoRequest args) {
        return service.calcularProrrateo(args);
    }
}

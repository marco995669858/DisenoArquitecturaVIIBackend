package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.periodoFacturacion.request.PeriodoFacturacionRequest;
import com.dataverse.rentals.model.periodoFacturacion.response.PeriodoFacturacionDTO;
import com.dataverse.rentals.service.ServicePeriodoFacturacion;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}periodo-facturacion")
@RequiredArgsConstructor
public class ControllerPeriodoFacturacion {

    private final ServicePeriodoFacturacion service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<PeriodoFacturacionDTO>> listarPeriodos() {
        return service.listarPeriodos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdatePeriodo(@RequestBody PeriodoFacturacionRequest args) {
        return service.insertUpdatePeriodo(args);
    }
}

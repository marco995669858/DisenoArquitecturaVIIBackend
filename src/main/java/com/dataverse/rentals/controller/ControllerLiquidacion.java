package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.liquidacion.request.GenerarLiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.request.LiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.response.GenerarLiquidacionResultDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;
import com.dataverse.rentals.service.ServiceLiquidacion;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}liquidacion")
@RequiredArgsConstructor
public class ControllerLiquidacion {

    private final ServiceLiquidacion service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<LiquidacionDTO>> listarLiquidaciones() {
        return service.listarLiquidaciones();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateLiquidacion(@RequestBody LiquidacionRequest args) {
        return service.insertUpdateLiquidacion(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarLiquidacion(@RequestBody LiquidacionRequest args) {
        return service.eliminarLiquidacion(args);
    }

    @PostMapping(value = "/generar")
    public ResponseDTO<GenerarLiquidacionResultDTO> generarLiquidaciones(@RequestBody GenerarLiquidacionRequest args) {
        return service.generarLiquidaciones(args);
    }

    @GetMapping(value = "/verificar-vencidas")
    public ResponseDTO<Integer> verificarVencidas() {
        return service.verificarVencidas();
    }
}

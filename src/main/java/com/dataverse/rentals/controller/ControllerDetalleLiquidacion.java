package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.detalleLiquidacion.request.DetalleLiquidacionRequest;
import com.dataverse.rentals.model.detalleLiquidacion.response.DetalleLiquidacionDTO;
import com.dataverse.rentals.service.ServiceDetalleLiquidacion;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}detalle-liquidacion")
@RequiredArgsConstructor
public class ControllerDetalleLiquidacion {

    private final ServiceDetalleLiquidacion service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<DetalleLiquidacionDTO>> listarPorLiquidacion(@RequestParam Integer idLiquidacion) {
        return service.listarPorLiquidacion(idLiquidacion);
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateDetalle(@RequestBody DetalleLiquidacionRequest args) {
        return service.insertUpdateDetalle(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarDetalle(@RequestBody DetalleLiquidacionRequest args) {
        return service.eliminarDetalle(args);
    }
}

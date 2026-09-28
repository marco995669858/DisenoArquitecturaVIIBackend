package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.pago.request.PagoRequest;
import com.dataverse.rentals.model.pago.response.PagoDTO;
import com.dataverse.rentals.service.ServicePago;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}pago")
@RequiredArgsConstructor
public class ControllerPago {

    private final ServicePago service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<PagoDTO>> listarPagos() {
        return service.listarPagos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdatePago(@RequestBody PagoRequest args) {
        return service.insertUpdatePago(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarPago(@RequestBody PagoRequest args) {
        return service.eliminarPago(args);
    }
}

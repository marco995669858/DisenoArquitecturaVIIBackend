package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.reciboServicio.request.ReciboServicioRequest;
import com.dataverse.rentals.model.reciboServicio.response.ReciboServicioDTO;
import com.dataverse.rentals.service.ServiceReciboServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}recibo-servicio")
@RequiredArgsConstructor
public class ControllerReciboServicio {

    private final ServiceReciboServicio service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<ReciboServicioDTO>> listarRecibos() {
        return service.listarRecibos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateRecibo(@RequestBody ReciboServicioRequest args) {
        return service.insertUpdateRecibo(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarRecibo(@RequestBody ReciboServicioRequest args) {
        return service.eliminarRecibo(args);
    }
}

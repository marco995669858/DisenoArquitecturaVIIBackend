package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.medidor.request.MedidorRequest;
import com.dataverse.rentals.model.medidor.response.MedidorDTO;
import com.dataverse.rentals.service.ServiceMedidor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}medidor")
@RequiredArgsConstructor
public class ControllerMedidor {

    private final ServiceMedidor service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<MedidorDTO>> listarMedidores() {
        return service.listarMedidores();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateMedidor(@RequestBody MedidorRequest args) {
        return service.insertUpdateMedidor(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarMedidor(@RequestBody MedidorRequest args) {
        return service.eliminarMedidor(args);
    }
}

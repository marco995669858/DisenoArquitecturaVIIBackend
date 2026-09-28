package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.lecturaMedidor.request.LecturaMedidorRequest;
import com.dataverse.rentals.model.lecturaMedidor.response.LecturaMedidorDTO;
import com.dataverse.rentals.service.ServiceLecturaMedidor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}lectura-medidor")
@RequiredArgsConstructor
public class ControllerLecturaMedidor {

    private final ServiceLecturaMedidor service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<LecturaMedidorDTO>> listarLecturas() {
        return service.listarLecturas();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateLectura(@RequestBody LecturaMedidorRequest args) {
        return service.insertUpdateLectura(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarLectura(@RequestBody LecturaMedidorRequest args) {
        return service.eliminarLectura(args);
    }
}

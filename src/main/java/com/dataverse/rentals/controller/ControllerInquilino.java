package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.inquilino.request.InquilinoRequest;
import com.dataverse.rentals.model.inquilino.response.InquilinoDTO;
import com.dataverse.rentals.service.ServiceInquilino;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}inquilino")
@RequiredArgsConstructor
public class ControllerInquilino {

    private final ServiceInquilino service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<InquilinoDTO>> listarInquilinos() {
        return service.listarInquilinos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateInquilino(@RequestBody InquilinoRequest args) {
        return service.insertUpdateInquilino(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarInquilino(@RequestBody InquilinoRequest args) {
        return service.eliminarInquilino(args);
    }
}

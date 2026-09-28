package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.auditoria.response.EventoAuditoriaDTO;
import com.dataverse.rentals.service.ServiceAuditoria;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}auditoria")
@RequiredArgsConstructor
public class ControllerAuditoria {

    private final ServiceAuditoria service;

    @GetMapping(value = "/historial")
    public ResponseDTO<List<EventoAuditoriaDTO>> historial(@RequestParam(defaultValue = "200") int limite) {
        return service.listarHistorial(limite);
    }
}

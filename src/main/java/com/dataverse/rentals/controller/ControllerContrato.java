package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.contrato.request.ContratoRequest;
import com.dataverse.rentals.model.contrato.request.RenovarContratoRequest;
import com.dataverse.rentals.model.contrato.response.AlertaVencimientoResultDTO;
import com.dataverse.rentals.model.contrato.response.ContratoDTO;
import com.dataverse.rentals.model.contrato.response.RenovarContratoResultDTO;
import com.dataverse.rentals.service.ServiceContrato;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}contrato")
@RequiredArgsConstructor
public class ControllerContrato {

    private final ServiceContrato service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<ContratoDTO>> listarContratos() {
        return service.listarContratos();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateContrato(@RequestBody ContratoRequest args) {
        return service.insertUpdateContrato(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarContrato(@RequestBody ContratoRequest args) {
        return service.eliminarContrato(args);
    }

    @GetMapping(value = "/alertas-vencimiento")
    public ResponseDTO<AlertaVencimientoResultDTO> alertasVencimiento() {
        return service.verificarVencimientos();
    }

    @PostMapping(value = "/renovar")
    public ResponseDTO<RenovarContratoResultDTO> renovarContrato(@RequestBody RenovarContratoRequest args) {
        return service.renovarContrato(args);
    }
}

package com.pe.consultorio.clinico.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.tipoDocumento.request.TipoDocumentoRequest;
import com.pe.consultorio.clinico.model.tipoDocumento.response.TipoDocumentoDTO;
import com.pe.consultorio.clinico.service.ServiceTipoDocumento;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${consultorio.clinico.controller.path}tipo/documento")
@RequiredArgsConstructor
public class ControllerTipoDocumento {

    private final ServiceTipoDocumento service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<TipoDocumentoDTO>> listarTipoDocumentos() {
        return service.listarTipoDocumentos();
    }

    @PostMapping(value = "/insertar/actulizar")
    public ResponseDTO<Integer> insertUpdateTipoDocumento(@RequestBody TipoDocumentoRequest args) {
        return service.insertUpdateTipoDocumento(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarTipoDocumento(@RequestBody TipoDocumentoRequest args) {
        return service.eliminarTipoDocumento(args);
    }

}

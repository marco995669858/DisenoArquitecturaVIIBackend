package com.dataverse.rentals.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.rol.request.RolRequest;
import com.dataverse.rentals.model.rol.response.RolDTO;
import com.dataverse.rentals.service.ServiceRol;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${dataverse.rentals.controller.path}rol")
@RequiredArgsConstructor
public class ControllerRol {

    private final ServiceRol service;

    @GetMapping(value = "/listar")
    public ResponseDTO<List<RolDTO>> listarRoles() {
        return service.listarRoles();
    }

    @PostMapping(value = "/insertar/actualizar")
    public ResponseDTO<Integer> insertUpdateRol(@RequestBody RolRequest args) {
        return service.insertUpdateRol(args);
    }

    @PostMapping(value = "/eliminar")
    public ResponseDTO<Integer> eliminarRol(@RequestBody RolRequest args) {
        return service.eliminarRol(args);
    }

    @GetMapping(value = "/permisos/{idRol}")
    public ResponseDTO<List<PermisoAsignadoDTO>> listarPermisosDeRol(@PathVariable Integer idRol) {
        return service.listarPermisosDeRol(idRol);
    }
}

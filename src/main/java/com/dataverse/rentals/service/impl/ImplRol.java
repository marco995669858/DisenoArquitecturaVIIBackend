package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dataverse.rentals.mapper.MapperRol;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.rol.request.RolRequest;
import com.dataverse.rentals.model.rol.response.RolDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceRol;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplRol implements ServiceRol {

    private final MapperRol mapper;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<RolDTO>> listarRoles() {
        try {
            List<RolDTO> data = mapper.listarRoles();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Integer> insertUpdateRol(RolRequest args) {
        try {
            boolean esNuevo = args.getIdRol() == null;
            if (esNuevo) {
                mapper.insertarRol(args, audit.usuarioActual(), audit.ipActual());
            } else {
                mapper.actualizarRol(args, audit.usuarioActual(), audit.ipActual());
            }

            sincronizarPermisos(args.getIdRol(), args.getPermisos());

            String mensaje = esNuevo ? "Rol registrado correctamente." : "Rol actualizado correctamente.";
            return new ResponseDTO<>(args.getIdRol(), false, mensaje);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * Los permisos de un rol se reemplazan por completo en cada guardado (se
     * borran todos y se insertan los que llegaron marcados), en vez de
     * calcular un diff: rol_permiso es una tabla tecnica de configuracion,
     * como refresh_token, no un registro de negocio que deba conservar
     * historial de que permiso se agrego o quito cuando.
     */
    private void sincronizarPermisos(Integer idRol, List<PermisoAsignadoDTO> permisos) {
        mapper.eliminarPermisosDeRol(idRol);
        if (!CollectionUtils.isEmpty(permisos)) {
            for (PermisoAsignadoDTO permiso : permisos) {
                // Ante un valor ausente o invalido, se falla hacia el lado
                // seguro (menos acceso), no hacia ESCRITURA.
                String nivel = "ESCRITURA".equals(permiso.getNivelAcceso()) ? "ESCRITURA" : "LECTURA";
                mapper.insertarPermiso(idRol, permiso.getCodigo(), nivel);
            }
        }
    }

    @Override
    public ResponseDTO<List<PermisoAsignadoDTO>> listarPermisosDeRol(Integer idRol) {
        try {
            return new ResponseDTO<>(mapper.listarPermisosDeRol(idRol), false, "OK");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarRol(RolRequest args) {
        try {
            if (mapper.contarUsuariosDeRol(args.getIdRol()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar el rol: hay usuarios con este rol asignado.");
            }

            int filas = mapper.eliminarRol(args.getIdRol(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el rol a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Rol eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}

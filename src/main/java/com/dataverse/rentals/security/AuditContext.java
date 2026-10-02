package com.dataverse.rentals.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Resuelve quién y desde dónde se está ejecutando la operación actual, para poblar las
 * columnas de auditoría (usuario_creacion/actualizacion/eliminacion, ip_*) sin confiar en
 * datos que pueda enviar el cliente en el payload.
 */
@Component
@RequiredArgsConstructor
public class AuditContext {

    private final HttpServletRequest request;

    public Integer usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getIdUsuario();
        }
        return null;
    }

    /**
     * Devuelve la IP real de la conexion TCP, nunca la de un header. Este
     * backend no corre detras de un proxy propio que reescriba
     * X-Forwarded-For con el valor real y descarte el que venga del
     * cliente -- si se leyera ese header aca, cualquiera podria falsificar
     * la IP que queda registrada en cada columna de auditoria (o eludir un
     * limitador de intentos por IP) con solo mandarlo en la peticion.
     */
    public String ipActual() {
        return request.getRemoteAddr();
    }
}

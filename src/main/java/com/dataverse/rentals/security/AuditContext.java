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

    public String ipActual() {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

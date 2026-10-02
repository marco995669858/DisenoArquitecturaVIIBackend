package com.dataverse.rentals.security;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Guardia general contra abuso/DoS basico: un limite amplio por IP real sobre
 * toda la API (no apunta a fuerza bruta de login en particular -- eso ya lo
 * cubre LoginAttemptTracker, con limites mucho mas estrictos). Ventana fija
 * de 1 minuto que se reinicia sola, sin bloqueo extendido: a diferencia del
 * login, aca no hay una "cuenta" que proteger, solo evitar que una sola IP
 * sature el backend.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_SOLICITUDES_POR_MINUTO = 150;
    private static final Duration VENTANA = Duration.ofMinutes(1);

    private static class Contador {
        final AtomicInteger cantidad = new AtomicInteger(0);
        volatile Instant inicioVentana = Instant.now();
    }

    private final ConcurrentHashMap<String, Contador> porIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Contador contador = porIp.computeIfAbsent(ip, k -> new Contador());

        synchronized (contador) {
            Instant ahora = Instant.now();
            if (Duration.between(contador.inicioVentana, ahora).compareTo(VENTANA) > 0) {
                contador.inicioVentana = ahora;
                contador.cantidad.set(0);
            }
            if (contador.cantidad.incrementAndGet() > MAX_SOLICITUDES_POR_MINUTO) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"data\":null,\"error\":true,\"mensaje\":\"Demasiadas solicitudes. Intente nuevamente en un momento.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

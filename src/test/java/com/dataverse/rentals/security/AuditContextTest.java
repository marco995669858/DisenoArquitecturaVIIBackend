package com.dataverse.rentals.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Este backend no corre detras de un proxy propio que reescriba
 * X-Forwarded-For con la IP real del cliente y descarte lo que el cliente
 * mismo mande -- por eso ipActual() debe ignorar ese header por completo
 * (es el cliente quien lo controla) y usar solo la IP real de la conexion
 * TCP. Sin este resguardo, cualquiera podria falsificar la IP que queda en
 * las columnas de auditoria, o eludir un limitador de intentos por IP, con
 * solo mandar el header que quiera.
 */
@ExtendWith(MockitoExtension.class)
class AuditContextTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuditContext auditContext;

    @Test
    void ignoraElHeaderXForwardedForAunqueElClienteLoEnvieFalsificado() {
        // Un atacante podria mandar este header con cualquier valor para
        // aparentar otra IP; ipActual() no debe ni siquiera consultarlo.
        when(request.getRemoteAddr()).thenReturn("203.0.113.9");

        String ip = auditContext.ipActual();

        assertThat(ip).isEqualTo("203.0.113.9");
        org.mockito.Mockito.verify(request, org.mockito.Mockito.never()).getHeader(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void usaSiempreLaIpRealDeLaConexion() {
        when(request.getRemoteAddr()).thenReturn("10.0.0.5");

        assertThat(auditContext.ipActual()).isEqualTo("10.0.0.5");
    }
}

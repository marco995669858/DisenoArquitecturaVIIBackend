package com.dataverse.rentals.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTest {

    private MockHttpServletRequest peticion(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/dataverse/rentals/api/unidad/listar");
        request.setRemoteAddr(ip);
        return request;
    }

    @Test
    void permiteSolicitudesPorDebajoDelLimite() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(peticion("1.1.1.1"), response, new MockFilterChain());
            assertThat(response.getStatus()).isNotEqualTo(429);
        }
    }

    @Test
    void rechazaConTooManyRequestsAlSuperarElLimitePorIp() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();
        MockHttpServletResponse ultima = null;

        for (int i = 0; i < 151; i++) {
            ultima = new MockHttpServletResponse();
            filter.doFilter(peticion("2.2.2.2"), ultima, new MockFilterChain());
        }

        assertThat(ultima.getStatus()).isEqualTo(429);
    }

    @Test
    void unaIpDistintaNoSeVeAfectadaPorElLimiteDeOtra() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        for (int i = 0; i < 151; i++) {
            filter.doFilter(peticion("3.3.3.3"), new MockHttpServletResponse(), new MockFilterChain());
        }

        MockHttpServletResponse respuestaOtraIp = new MockHttpServletResponse();
        filter.doFilter(peticion("4.4.4.4"), respuestaOtraIp, new MockFilterChain());

        assertThat(respuestaOtraIp.getStatus()).isNotEqualTo(429);
    }
}

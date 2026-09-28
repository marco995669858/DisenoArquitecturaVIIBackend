package com.dataverse.rentals.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataverse.rentals.mapper.MapperUsuario;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioLoginDTO;

/**
 * Que el frontend oculte los botones de un rol de solo lectura no evita que
 * alguien llame al endpoint de escritura a mano (curl, Postman). Este filtro
 * es lo que realmente lo impide, asi que se prueba contra una peticion HTTP
 * simulada real (MockHttpServletRequest/Response), no solo llamando al metodo
 * Java, para verificar el codigo de estado que efectivamente le llega al
 * cliente.
 */
@ExtendWith(MockitoExtension.class)
class PermisoAccessFilterTest {

    @Mock
    private MapperUsuario mapperUsuario;

    @InjectMocks
    private PermisoAccessFilter filter;

    private MockFilterChain chain;
    private MockHttpServletResponse response;

    @BeforeEach
    void configurarRutaBase() {
        ReflectionTestUtils.setField(filter, "basePath", "/dataverse/rentals/api/");
        chain = new MockFilterChain();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiarContextoDeSeguridad() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(Integer idUsuario) {
        UsuarioLoginDTO dto = new UsuarioLoginDTO(idUsuario, "usuario-test", "hash-no-relevante", true, "ROLE_CONSULTA");
        CustomUserDetails userDetails = new CustomUserDetails(dto);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }

    private MockHttpServletRequest peticion(String metodo, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(metodo, path);
        request.setRequestURI(path);
        return request;
    }

    @Test
    void permiteUnGetSiElUsuarioTieneElModuloConCualquierNivel() throws Exception {
        autenticarComo(1);
        when(mapperUsuario.listarPermisosDeUsuario(1)).thenReturn(List.of(new PermisoAsignadoDTO("UNIDADES", "LECTURA")));

        filter.doFilter(peticion("GET", "/dataverse/rentals/api/unidad/listar"), response, chain);

        assertThat(chain.getRequest()).isNotNull(); // llego al siguiente filtro, no se corto
        assertThat(response.getStatus()).isNotEqualTo(403);
    }

    @Test
    void rechazaUnGetSiElUsuarioNoTieneElModuloAsignado() throws Exception {
        autenticarComo(1);
        when(mapperUsuario.listarPermisosDeUsuario(1)).thenReturn(List.of(new PermisoAsignadoDTO("MANTENIMIENTO", "ESCRITURA")));

        filter.doFilter(peticion("GET", "/dataverse/rentals/api/unidad/listar"), response, chain);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(chain.getRequest()).isNull(); // nunca llego al controller
    }

    @Test
    void rechazaUnPostDeEscrituraSiElNivelEsSoloLectura() throws Exception {
        autenticarComo(1);
        when(mapperUsuario.listarPermisosDeUsuario(1)).thenReturn(List.of(new PermisoAsignadoDTO("UNIDADES", "LECTURA")));

        filter.doFilter(peticion("POST", "/dataverse/rentals/api/unidad/insertar/actualizar"), response, chain);

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void permiteUnPostDeEscrituraSiElNivelEsEscritura() throws Exception {
        autenticarComo(1);
        when(mapperUsuario.listarPermisosDeUsuario(1)).thenReturn(List.of(new PermisoAsignadoDTO("UNIDADES", "ESCRITURA")));

        filter.doFilter(peticion("POST", "/dataverse/rentals/api/unidad/insertar/actualizar"), response, chain);

        assertThat(response.getStatus()).isNotEqualTo(403);
    }

    @Test
    void permiteElAutoservicioDeLaPropiaCuentaAunSinPermisoEnUsuarios() throws Exception {
        autenticarComo(1);
        // Ni siquiera deberia consultarse el mapper: esta ruta es autoservicio.

        filter.doFilter(peticion("POST", "/dataverse/rentals/api/usuario/cambiar-mi-password"), response, chain);

        assertThat(response.getStatus()).isNotEqualTo(403);
        verify(mapperUsuario, never()).listarPermisosDeUsuario(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void rechazaGestionarOtrosUsuariosSinPermisoEnUsuarios() throws Exception {
        autenticarComo(1);
        when(mapperUsuario.listarPermisosDeUsuario(1)).thenReturn(List.of());

        filter.doFilter(peticion("POST", "/dataverse/rentals/api/usuario/insertar/actualizar"), response, chain);

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void dejaPasarRutasSinModuloAsociadoComoAuthOModulo() throws Exception {
        filter.doFilter(peticion("POST", "/dataverse/rentals/api/auth/login"), response, chain);

        assertThat(response.getStatus()).isNotEqualTo(403);
        verify(mapperUsuario, never()).listarPermisosDeUsuario(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void dejaPasarSiNoHayUsuarioAutenticadoTodavia() throws Exception {
        // Sin Authentication en el contexto: lo resuelve authorizeHttpRequests despues, no este filtro.
        filter.doFilter(peticion("GET", "/dataverse/rentals/api/unidad/listar"), response, chain);

        assertThat(response.getStatus()).isNotEqualTo(403);
        verify(mapperUsuario, never()).listarPermisosDeUsuario(org.mockito.ArgumentMatchers.anyInt());
    }
}

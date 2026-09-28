package com.dataverse.rentals.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dataverse.rentals.mapper.MapperUsuario;
import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Hace cumplir de verdad los permisos por rol (RNF-01): que el frontend
 * oculte los botones de un rol de solo lectura no evita que alguien llame al
 * endpoint de escritura directamente. Este filtro corre despues de
 * JwtAuthenticationFilter (ya sabe quien es el usuario) y antes de que la
 * peticion llegue al controller.
 *
 * Regla: GET siempre esta permitido si el usuario tiene el modulo asignado
 * (con cualquier nivel); cualquier otro metodo (en este API, siempre POST
 * para insertar/actualizar/eliminar/generar/etc.) exige nivel ESCRITURA. Si
 * el modulo del recurso no tiene ningun permiso asignado, se rechaza
 * igual -- asi un rol "Consulta" no solo no ve el modulo en el menu, tampoco
 * puede leerlo ni escribirlo llamando a la API a mano.
 */
@Component
@RequiredArgsConstructor
public class PermisoAccessFilter extends OncePerRequestFilter {

    @Value("${dataverse.rentals.controller.path}")
    private String basePath;

    private final MapperUsuario mapperUsuario;

    // Primer segmento de la ruta (el nombre del recurso en @RequestMapping de
    // cada controller) -> codigo de modulo_sistema. Los recursos que no
    // aparecen aca (auth, modulo, reporte, auditoria) no se restringen por
    // este filtro: auth es publico, modulo/reporte son catalogos/exportes de
    // solo lectura sin CRUD propio, y auditoria ya es de solo lectura.
    private static final Map<String, String> RECURSO_A_MODULO = Map.ofEntries(
            Map.entry("unidad", "UNIDADES"),
            Map.entry("inquilino", "INQUILINOS"),
            Map.entry("contrato", "CONTRATOS"),
            Map.entry("medidor", "MEDIDORES"),
            Map.entry("lectura-medidor", "LECTURAS"),
            Map.entry("recibo-servicio", "RECIBOS"),
            Map.entry("prorrateo", "PRORRATEO"),
            Map.entry("liquidacion", "LIQUIDACIONES"),
            Map.entry("detalle-liquidacion", "LIQUIDACIONES"),
            Map.entry("pago", "PAGOS"),
            Map.entry("mantenimiento", "MANTENIMIENTO"),
            Map.entry("usuario", "USUARIOS"),
            Map.entry("rol", "ROLES"),
            Map.entry("parametro-sistema", "PARAMETROS"),
            Map.entry("periodo-facturacion", "PERIODOS")
    );

    // Subrutas de /usuario/ que son autoservicio (cualquier usuario
    // autenticado las necesita sobre SU PROPIA cuenta, sin importar si su rol
    // tiene el modulo USUARIOS asignado): cambiar su clave, o activar,
    // confirmar, desactivar o consultar SU propio MFA.
    private static final Set<String> RUTAS_USUARIO_AUTOSERVICIO = Set.of(
            "cambiar-mi-password", "mfa/estado", "mfa/activar", "mfa/confirmar", "mfa/desactivar", "mis-permisos"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            // Sin usuario autenticado todavia: no es cosa de este filtro --
            // lo resuelve (o rechaza) la regla authorizeHttpRequests de
            // SecurityConfig mas adelante en la cadena.
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        if (!uri.startsWith(basePath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String resto = uri.substring(basePath.length());
        int barra = resto.indexOf('/');
        String recurso = barra >= 0 ? resto.substring(0, barra) : resto;
        String subRuta = barra >= 0 ? resto.substring(barra + 1) : "";

        String codigoModulo = RECURSO_A_MODULO.get(recurso);
        if (codigoModulo == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("usuario".equals(recurso) && RUTAS_USUARIO_AUTOSERVICIO.contains(subRuta)) {
            filterChain.doFilter(request, response);
            return;
        }

        List<PermisoAsignadoDTO> permisos = mapperUsuario.listarPermisosDeUsuario(userDetails.getIdUsuario());
        PermisoAsignadoDTO permiso = permisos.stream()
                .filter(p -> codigoModulo.equals(p.getCodigo()))
                .findFirst()
                .orElse(null);

        boolean esEscritura = !"GET".equalsIgnoreCase(request.getMethod());
        boolean tieneAcceso = permiso != null && (!esEscritura || "ESCRITURA".equals(permiso.getNivelAcceso()));

        if (!tieneAcceso) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"data\":null,\"error\":true,\"mensaje\":\"No tiene permiso para realizar esta acción.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

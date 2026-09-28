package com.dataverse.rentals.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final PermisoAccessFilter permisoAccessFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigin;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, PermisoAccessFilter permisoAccessFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.permisoAccessFilter = permisoAccessFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF porque usamos JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Nuestro CORS robusto
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/dataverse/rentals/api/auth/**").permitAll() // Endpoints públicos (Login)
                .anyRequest().authenticated() // Todo lo demás requiere token
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No guardamos sesión en el servidor
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(permisoAccessFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // Unica fuente de configuracion CORS del backend (antes habia una segunda,
    // en CorsConfig.java via WebMvcConfigurer, que quedaba sin efecto porque
    // Spring Security intercepta la peticion antes y aplica esta en su lugar;
    // se elimino para no mantener dos configuraciones que podian divergir).
    // El origen permitido sale de app.cors.allowed-origins (variable de
    // entorno ORIGINS_URL), no queda fijo en el codigo.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Importante si manejas cookies o tokens en cabeceras seguras

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.pe.consultorio.clinico.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String url;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. Aplica a absolutamente todos tus controladores y endpoints
                .allowedOrigins(url) // 2. La URL exacta de tu Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. Métodos HTTP permitidos
                .allowedHeaders("*") // 4. Permite cualquier cabecera (incluyendo JSON)
                .allowCredentials(true); // 5. Permite el envío de credenciales/cookies
    }
}

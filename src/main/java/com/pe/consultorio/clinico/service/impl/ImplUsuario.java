package com.pe.consultorio.clinico.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperUsuario;
import com.pe.consultorio.clinico.model.ResponseDTO;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.usuario.request.LoginRequest;
import com.pe.consultorio.clinico.model.usuario.request.TokenRequest;
import com.pe.consultorio.clinico.model.usuario.response.AuthDTO;
import com.pe.consultorio.clinico.model.usuario.response.RefreshTokenDTO;
import com.pe.consultorio.clinico.security.CustomUserDetails;
import com.pe.consultorio.clinico.security.CustomUserDetailsService;
import com.pe.consultorio.clinico.security.JwtService;
import com.pe.consultorio.clinico.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplUsuario implements ServiceUsuario {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService detailsService;
    private final MapperUsuario mapper;

    // El Refresh Token durará 8 horas (en milisegundos)
    private final long REFRESH_TOKEN_EXPIRATION_MS = 28800000L;

    @Override
    public ResponseDTO<AuthDTO> login(LoginRequest args) {
        try {
            // 1. Spring valida credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(args.username(), args.password())
            );

            // 2. Traemos la data completa del usuario
            CustomUserDetails user = (CustomUserDetails) detailsService.loadUserByUsername(args.username());

            // 3. Generamos el token
            String token = jwtService.generateToken(user, user.getIdEmpresa(), user.getTipoNegocio(), user.getIdUsuario());

            // 4. Retornamos éxito (Simplificando la creación de variables locales)
            return new ResponseDTO<>(new AuthDTO(token), false, "Login exitoso");

        } catch (BadCredentialsException e) {
            // Error controlado: Contraseña o usuario incorrecto
            log.warn("Intento de login fallido para el usuario: {}", args.username());
            // Mostramos un mensaje amigable, nunca revelamos si fue el usuario o la clave lo que falló
            return new ResponseDTO<>(null, true, "Usuario o contraseña incorrectos");

        } catch (AuthenticationException e) {
            // Error crítico: Base de datos caída, error de red, etc.
            log.error("Error inesperado durante el login del usuario {}: {}", args.username(), e.getMessage(), e);
            // Mensaje genérico para no filtrar información del servidor
            return new ResponseDTO<>(null, true, "Error interno del servidor. Por favor, intente más tarde.");
        }
    }

    public RefreshTokenDTO crearRefreshToken(TokenRequest args) {
        ResultCustomRUD<TokenRequest> token = new ResultCustomRUD<>();
        token.setData(args);
        // 1. (Opcional) Borramos tokens anteriores de este usuario para que solo tenga una sesión activa
        mapper.eliminarToken(token);

        // 2. Creamos el nuevo token
        RefreshTokenDTO refreshToken = new RefreshTokenDTO(
                null,
                args.getIdUsuario(),
                args.getUsername(),
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS),
                args.getIdEmpresa()
        );

        // 3. Lo guardamos en Oracle
        mapper.insertarToken(token);

        return refreshToken;
    }

    public RefreshTokenDTO verificarExpiracion(RefreshTokenDTO token) {
        ResultCustomRUD<TokenRequest> args = new ResultCustomRUD<>();
        args.getData().setIdUsuario(token.getIdUsuario());
        // Si la fecha de expiración es menor a la fecha actual (ya pasó)
        if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
            mapper.eliminarToken(args);
            throw new RuntimeException("El Refresh Token ha expirado. Por favor, inicie sesión nuevamente.");
        }
        return token;
    }

}

package com.dataverse.rentals.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dataverse.rentals.mapper.MapperUsuario;
import com.dataverse.rentals.model.usuario.response.UsuarioLoginDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MapperUsuario mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioLoginDTO usuarioDTO = mapper.obtenerUsuarioLogin(username);

        if (usuarioDTO == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return new CustomUserDetails(usuarioDTO);
    }
}

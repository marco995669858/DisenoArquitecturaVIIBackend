package com.pe.consultorio.clinico.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pe.consultorio.clinico.model.usuario.response.UsuarioLoginDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UsuarioLoginDTO usuarioDTO;

    // --- Métodos personalizados para nuestro Multi-Tenant ---
    public Integer getIdEmpresa() {
        return usuarioDTO.getIdEmpresa();
    }

    public String getTipoNegocio() {
        return usuarioDTO.getTipoNegocio();
    }

    public Integer getIdUsuario(){
        return usuarioDTO.getIdUsuario();
    }

    // --- Métodos obligatorios de UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Rompemos el String "ROLE_ADMIN,ROLE_MEDICO" en una lista de autoridades
        return Arrays.stream(usuarioDTO.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuarioDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return usuarioDTO.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuarioDTO.getActivo();
    }
}

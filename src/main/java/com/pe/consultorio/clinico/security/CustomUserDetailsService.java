package com.pe.consultorio.clinico.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pe.consultorio.clinico.mapper.MapperUsuario;
import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.usuario.response.UsuarioLoginDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MapperUsuario mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Instanciamos tu objeto contenedor (asegúrate de que tenga una lista interna)
        ResultCustomPage<UsuarioLoginDTO> outData = new ResultCustomPage<>();

        // 2. Llamamos al SP. El método retorna void, pero rellena 'outData' mágicamente
        mapper.obtenerUsuarioLogin(username, outData);

        // 3. Extraemos la lista del objeto outData (asumo que tiene un getter para su lista)
        List<UsuarioLoginDTO> resultados = outData.getData(); // o como se llame tu método

        // 4. Validamos
        if (resultados == null || resultados.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        // 5. Como es login, solo nos interesa el primero
        UsuarioLoginDTO usuarioDTO = resultados.get(0);

        return new CustomUserDetails(usuarioDTO);
    }
}

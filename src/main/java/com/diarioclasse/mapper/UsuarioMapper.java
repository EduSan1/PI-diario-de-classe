package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.UsuarioResponse;
import com.diarioclasse.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getTipoUsuario().name(),
                usuario.getAtivo(),
                usuario.getDataCriacao()
        );
    }
}

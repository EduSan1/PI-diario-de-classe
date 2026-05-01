package com.diarioclasse.service;

import com.diarioclasse.dto.request.LoginRequest;
import com.diarioclasse.dto.response.LoginResponse;
import com.diarioclasse.exception.AcessoNegadoException;
import com.diarioclasse.exception.CredenciaisInvalidasException;
import com.diarioclasse.model.Usuario;
import com.diarioclasse.repository.UsuarioRepository;
import com.diarioclasse.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final long expiration;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       @Value("${jwt.expiration}") long expiration) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.expiration = expiration;
    }

    public LoginResponse login(LoginRequest request) {
        String usuarioNormalizado = request.usuario().toLowerCase();

        Usuario usuario = usuarioRepository.findByUsuario(usuarioNormalizado)
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário ou senha inválidos"));

        if (!usuario.getAtivo()) {
            throw new AcessoNegadoException("Usuário inativo");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Usuário ou senha inválidos");
        }

        String token = jwtUtil.gerarToken(
                usuario.getUsuario(),
                usuario.getTipoUsuario().name(),
                usuario.getId().longValue()
        );

        return new LoginResponse(token, "Bearer", usuario.getUsuario(), usuario.getTipoUsuario().name(), expiration);
    }
}

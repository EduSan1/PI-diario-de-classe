package com.diarioclasse.repository;

import com.diarioclasse.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    org.springframework.data.domain.Page<Usuario> findAllByAtivo(boolean ativo, org.springframework.data.domain.Pageable pageable);
}

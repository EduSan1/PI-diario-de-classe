package com.diarioclasse.repository;

import com.diarioclasse.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByUsuarioId(Integer usuarioId);
}

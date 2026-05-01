package com.diarioclasse.repository;

import com.diarioclasse.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    Optional<Aluno> findByUsuarioId(Integer usuarioId);

    List<Aluno> findByTurmaId(Integer idTurma);

    long countByTurmaId(Integer idTurma);
}


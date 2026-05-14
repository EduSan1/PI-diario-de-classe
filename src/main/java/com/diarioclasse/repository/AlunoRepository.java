package com.diarioclasse.repository;

import com.diarioclasse.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    Optional<Aluno> findByUsuarioId(Integer usuarioId);

    List<Aluno> findByTurmaId(Integer idTurma);

    long countByTurmaId(Integer idTurma);

    @Query("""
            SELECT a FROM Aluno a
            WHERE a.turma.id IN (
                SELECT DISTINCT t.id FROM Turma t
                WHERE t.professorRegente.id = :idProfessor
                   OR EXISTS (
                       SELECT 1 FROM TurmaMateria tm
                       WHERE tm.turma.id = t.id
                         AND tm.professor.id = :idProfessor
                   )
            )
            """)
    Page<Aluno> findAllByProfessor(Integer idProfessor, Pageable pageable);
}


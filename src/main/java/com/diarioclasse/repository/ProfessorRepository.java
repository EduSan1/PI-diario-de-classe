package com.diarioclasse.repository;

import com.diarioclasse.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    Optional<Professor> findByUsuarioId(Integer usuarioId);

    @Query(
            value = "SELECT DISTINCT p FROM Professor p LEFT JOIN FETCH p.materias pm LEFT JOIN FETCH pm.materia",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Professor p"
    )
    Page<Professor> findAllWithMaterias(Pageable pageable);

    @Query("SELECT p FROM Professor p LEFT JOIN FETCH p.materias pm LEFT JOIN FETCH pm.materia WHERE p.id = :id")
    Optional<Professor> findByIdWithMaterias(Integer id);
}



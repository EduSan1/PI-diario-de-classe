package com.diarioclasse.repository;

import com.diarioclasse.model.Materia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Integer id);

    @Query("""
            SELECT m FROM Materia m
            WHERE EXISTS (
                SELECT 1 FROM ProfessorMateria pm
                WHERE pm.materia.id = m.id
                  AND pm.professor.id = :idProfessor
            )
            """)
    Page<Materia> findAllByProfessor(Integer idProfessor, Pageable pageable);
}

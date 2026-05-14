package com.diarioclasse.repository;

import com.diarioclasse.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TurmaRepository extends JpaRepository<Turma, Integer> {

    boolean existsBySerieEscolarAndLetraTurmaAndAno(Integer serieEscolar, String letraTurma, Integer ano);

    boolean existsBySerieEscolarAndLetraTurmaAndAnoAndIdNot(Integer serieEscolar, String letraTurma, Integer ano, Integer id);

    @Query("""
            SELECT DISTINCT t FROM Turma t
            WHERE t.professorRegente.id = :idProfessor
               OR EXISTS (
                   SELECT 1 FROM TurmaMateria tm
                   WHERE tm.turma.id = t.id
                     AND tm.professor.id = :idProfessor
               )
            """)
    Page<Turma> findAllByProfessor(Integer idProfessor, Pageable pageable);
}

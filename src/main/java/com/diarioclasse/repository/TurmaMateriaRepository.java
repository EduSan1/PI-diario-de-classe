package com.diarioclasse.repository;

import com.diarioclasse.model.TurmaMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TurmaMateriaRepository extends JpaRepository<TurmaMateria, Integer> {

    boolean existsByTurmaIdAndMateriaIdAndProfessorId(Integer idTurma, Integer idMateria, Integer idProfessor);

    Optional<TurmaMateria> findByTurmaIdAndMateriaId(Integer idTurma, Integer idMateria);

    @Query("SELECT tm FROM TurmaMateria tm JOIN FETCH tm.materia JOIN FETCH tm.professor p JOIN FETCH p.usuario WHERE tm.turma.id = :idTurma")
    List<TurmaMateria> findAllByTurmaId(Integer idTurma);
}

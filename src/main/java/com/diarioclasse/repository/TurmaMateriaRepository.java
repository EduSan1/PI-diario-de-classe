package com.diarioclasse.repository;

import com.diarioclasse.model.TurmaMateria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaMateriaRepository extends JpaRepository<TurmaMateria, Integer> {

    boolean existsByTurmaIdAndMateriaIdAndProfessorId(Integer idTurma, Integer idMateria, Integer idProfessor);

    Optional<TurmaMateria> findByTurmaIdAndMateriaId(Integer idTurma, Integer idMateria);
}

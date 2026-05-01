package com.diarioclasse.repository;

import com.diarioclasse.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Integer> {

    boolean existsBySerieEscolarAndLetraTurmaAndAno(Integer serieEscolar, String letraTurma, Integer ano);

    boolean existsBySerieEscolarAndLetraTurmaAndAnoAndIdNot(Integer serieEscolar, String letraTurma, Integer ano, Integer id);
}

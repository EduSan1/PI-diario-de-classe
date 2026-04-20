package com.diarioclasse.repository;

import com.diarioclasse.model.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresencaRepository extends JpaRepository<Presenca, Integer> {

    boolean existsByAlunoIdAndMateriaIdAndData(Integer idAluno, Integer idMateria, LocalDate data);

    List<Presenca> findByAlunoId(Integer idAluno);

    List<Presenca> findByAlunoIdAndMateriaId(Integer idAluno, Integer idMateria);

    @Query("SELECT p FROM Presenca p WHERE p.aluno.turma.id = :idTurma AND p.data = :data")
    List<Presenca> findByTurmaIdAndData(Integer idTurma, LocalDate data);

    @Query("SELECT p FROM Presenca p WHERE p.aluno.usuario.id = :idUsuario")
    List<Presenca> findByUsuarioId(Integer idUsuario);
}

package com.diarioclasse.repository;

import com.diarioclasse.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    boolean existsByAlunoIdAndMateriaId(Integer idAluno, Integer idMateria);

    List<Nota> findByAlunoId(Integer idAluno);

    Optional<Nota> findByAlunoIdAndMateriaId(Integer idAluno, Integer idMateria);

    @Query("""
        SELECT n FROM Nota n
        JOIN n.aluno a
        WHERE a.turma.id = :idTurma
        ORDER BY a.id, n.materia.id
    """)
    List<Nota> findByTurmaId(@Param("idTurma") Integer idTurma);

    @Query("""
        SELECT n FROM Nota n
        JOIN n.aluno a
        WHERE a.turma.id = :idTurma
          AND n.materia.id = :idMateria
    """)
    List<Nota> findByTurmaIdAndMateriaId(@Param("idTurma") Integer idTurma,
                                          @Param("idMateria") Integer idMateria);
}

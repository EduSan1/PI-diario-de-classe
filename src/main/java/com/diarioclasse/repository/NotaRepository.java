package com.diarioclasse.repository;

import com.diarioclasse.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    boolean existsByAlunoIdAndMateriaId(Integer idAluno, Integer idMateria);

    List<Nota> findByAlunoId(Integer idAluno);

    @Query("""
        SELECT n FROM Nota n
        JOIN n.aluno a
        WHERE a.turma.id = :idTurma
        ORDER BY a.id, n.materia.id
    """)
    List<Nota> findByTurmaId(@Param("idTurma") Integer idTurma);
}

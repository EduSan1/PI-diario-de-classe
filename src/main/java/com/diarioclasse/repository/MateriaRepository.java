package com.diarioclasse.repository;

import com.diarioclasse.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Integer id);
}

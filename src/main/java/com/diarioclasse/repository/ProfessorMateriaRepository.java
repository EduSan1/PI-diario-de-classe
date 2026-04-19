package com.diarioclasse.repository;

import com.diarioclasse.model.ProfessorMateria;
import com.diarioclasse.model.ProfessorMateriaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorMateriaRepository extends JpaRepository<ProfessorMateria, ProfessorMateriaId> {

    boolean existsByIdIdMateria(Integer idMateria);
}

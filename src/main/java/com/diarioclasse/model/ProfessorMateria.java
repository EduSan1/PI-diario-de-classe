package com.diarioclasse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "professor_materias")
@Getter
@Setter
public class ProfessorMateria {

    @EmbeddedId
    private ProfessorMateriaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProfessor")
    @JoinColumn(name = "id_professor")
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idMateria")
    @JoinColumn(name = "id_materia")
    private Materia materia;
}

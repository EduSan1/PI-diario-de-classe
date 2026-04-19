package com.diarioclasse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ProfessorMateriaId implements Serializable {

    @Column(name = "id_professor")
    private Integer idProfessor;

    @Column(name = "id_materia")
    private Integer idMateria;
}

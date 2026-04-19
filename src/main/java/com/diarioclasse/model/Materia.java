package com.diarioclasse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "materias")
@Getter
@Setter
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Column(name = "nota_de_corte", nullable = false)
    private Integer notaDeCorte;

    @Column(nullable = false)
    private Boolean obrigatoria;
}

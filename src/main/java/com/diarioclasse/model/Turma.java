package com.diarioclasse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "serie_escolar", nullable = false)
    private Integer serieEscolar;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "letra_turma", nullable = false, columnDefinition = "CHAR(1)")
    private String letraTurma;

    @Column(nullable = false)
    private Integer ano;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "periodo_enum")
    private Periodo periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor_regente")
    private Professor professorRegente;

    @Column(name = "sala_fisica", length = 10)
    private String salaFisica;

    @Column(name = "max_alunos")
    private Integer maxAlunos;

}

package com.diarioclasse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(unique = true, length = 15)
    private String ra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turma")
    private Turma turma;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "nome_responsavel", length = 100)
    private String nomeResponsavel;

    @Column(name = "telefone_responsavel", length = 20)
    private String telefoneResponsavel;
}

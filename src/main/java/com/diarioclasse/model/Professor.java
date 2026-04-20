package com.diarioclasse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "registro_funcionario", unique = true, length = 20)
    private String registroFuncionario;

    @Column(name = "formacao_academica", length = 100)
    private String formacaoAcademica;

    @Column(name = "data_contratacao")
    private LocalDate dataContratacao;

    @Column(name = "especialidade_principal", length = 50)
    private String especialidadePrincipal;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProfessorMateria> materias = new ArrayList<>();
}

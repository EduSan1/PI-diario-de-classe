package com.diarioclasse.dto.request;

import com.diarioclasse.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de usuário")
public record CriarUsuarioRequest(

        @NotBlank
        @Size(max = 100)
        @Schema(example = "João da Silva")
        String nome,

        @NotBlank
        @Size(max = 50)
        @Schema(example = "prof.joao")
        String usuario,

        @NotBlank
        @Schema(example = "senha123")
        String senha,

        @NotNull
        @Schema(example = "PROFESSOR")
        TipoUsuario tipoUsuario,

        @Valid
        DadosAdministradorRequest dadosAdministrador,

        @Valid
        DadosProfessorRequest dadosProfessor,

        @Valid
        DadosAlunoRequest dadosAluno
) {}

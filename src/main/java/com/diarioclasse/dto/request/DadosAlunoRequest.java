package com.diarioclasse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados específicos do aluno")
public record DadosAlunoRequest(

        @NotBlank
        @Size(max = 15)
        @Schema(example = "RA-2026-001")
        String ra,

        @Size(max = 100)
        @Schema(example = "Maria da Silva")
        String nomeResponsavel,

        @Size(max = 20)
        @Schema(example = "11988888888")
        String telefoneResponsavel
) {}

package com.diarioclasse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados específicos do professor")
public record DadosProfessorRequest(

        @NotBlank
        @Size(max = 20)
        @Schema(example = "REG-001")
        String registroFuncionario,

        @Size(max = 100)
        @Schema(example = "Licenciatura em Matemática")
        String formacaoAcademica,

        @Size(max = 50)
        @Schema(example = "Matemática")
        String especialidadePrincipal
) {}

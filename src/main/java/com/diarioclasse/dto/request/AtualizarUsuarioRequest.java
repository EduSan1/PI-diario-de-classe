package com.diarioclasse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização de usuário")
public record AtualizarUsuarioRequest(

        @NotBlank
        @Size(max = 100)
        @Schema(example = "João da Silva Atualizado")
        String nome,

        @NotBlank
        @Size(max = 50)
        @Schema(example = "prof.joao2")
        String usuario
) {}

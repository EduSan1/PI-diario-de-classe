package com.diarioclasse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para autenticação no sistema")
public record LoginRequest(

        @NotBlank(message = "usuario: não deve estar em branco")
        @Schema(description = "Nome de usuário (case-insensitive)", example = "prof.joao")
        String usuario,

        @NotBlank(message = "senha: não deve estar em branco")
        @Schema(description = "Senha do usuário", example = "minhasenha123")
        String senha
) {}

package com.diarioclasse.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT")
public record LoginResponse(

        @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tipo,

        @Schema(description = "Nome de usuário autenticado", example = "prof.joao")
        String usuario,

        @Schema(description = "Perfil do usuário", example = "PROFESSOR")
        String perfil,

        @Schema(description = "Tempo de expiração em milissegundos", example = "86400000")
        long expiresIn
) {}

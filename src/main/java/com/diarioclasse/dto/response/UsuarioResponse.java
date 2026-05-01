package com.diarioclasse.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dados do usuário")
public record UsuarioResponse(

        @Schema(example = "1")
        Integer id,

        @Schema(example = "João da Silva")
        String nome,

        @Schema(example = "prof.joao")
        String usuario,

        @Schema(example = "PROFESSOR")
        String tipoUsuario,

        @Schema(example = "true")
        Boolean ativo,

        LocalDateTime dataCriacao
) {}

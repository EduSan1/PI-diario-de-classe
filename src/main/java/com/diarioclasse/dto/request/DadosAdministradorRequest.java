package com.diarioclasse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados específicos do administrador")
public record DadosAdministradorRequest(

        @Size(max = 50)
        @Schema(example = "Coordenação Pedagógica")
        String departamento,

        @Size(max = 20)
        @Schema(example = "11999999999")
        String contatoEmergencia
) {}

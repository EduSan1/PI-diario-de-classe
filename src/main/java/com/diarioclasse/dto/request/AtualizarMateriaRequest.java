package com.diarioclasse.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarMateriaRequest(
        @NotBlank String nome,
        @NotNull @Min(1) Integer cargaHoraria,
        @NotNull @Min(0) @Max(10) Integer notaDeCorte,
        Boolean obrigatoria
) {}

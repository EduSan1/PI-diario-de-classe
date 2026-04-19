package com.diarioclasse.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarMateriaRequest(
        @NotBlank String nome,
        @NotNull @Min(value = 1, message = "cargaHoraria deve ser maior que zero") Integer cargaHoraria,
        @NotNull @Min(value = 0, message = "notaDeCorte deve estar entre 0 e 10") @Max(value = 10, message = "notaDeCorte deve estar entre 0 e 10") Integer notaDeCorte,
        Boolean obrigatoria
) {}

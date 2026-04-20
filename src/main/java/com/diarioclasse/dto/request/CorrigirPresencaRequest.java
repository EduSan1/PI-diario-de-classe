package com.diarioclasse.dto.request;

import jakarta.validation.constraints.NotNull;

public record CorrigirPresencaRequest(
        @NotNull(message = "presente é obrigatório")
        Boolean presente,

        String observacao
) {}

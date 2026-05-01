package com.diarioclasse.dto.request;

import jakarta.validation.constraints.NotNull;

public record ItemPresencaRequest(
        @NotNull(message = "idAluno é obrigatório")
        Integer idAluno,

        @NotNull(message = "presente é obrigatório")
        Boolean presente,

        String observacao
) {}

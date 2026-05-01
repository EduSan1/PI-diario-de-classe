package com.diarioclasse.dto.request;

import jakarta.validation.constraints.NotNull;

public record VincularTurmaRequest(
        @NotNull(message = "idTurma é obrigatório — para desativar um aluno, utilize DELETE /usuarios/{id}")
        Integer idTurma
) {}

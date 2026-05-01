package com.diarioclasse.dto.request;

import com.diarioclasse.model.Periodo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarTurmaRequest(
        @NotNull Integer serieEscolar,
        @NotBlank String letraTurma,
        @NotNull Integer ano,
        @NotNull Periodo periodo,
        Integer idProfessorRegente,
        String salaFisica,
        Integer maxAlunos
) {
}

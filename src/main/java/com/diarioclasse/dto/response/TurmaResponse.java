package com.diarioclasse.dto.response;

import com.diarioclasse.model.Periodo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TurmaResponse(
        Integer id,
        Integer serieEscolar,
        String letraTurma,
        Integer ano,
        Periodo periodo,
        ProfessorResumoResponse professorRegente,
        String salaFisica,
        Integer maxAlunos
) {
}

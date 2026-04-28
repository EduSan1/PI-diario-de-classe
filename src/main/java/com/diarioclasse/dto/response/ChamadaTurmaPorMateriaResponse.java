package com.diarioclasse.dto.response;

import java.util.List;

public record ChamadaTurmaPorMateriaResponse(
        String materia,
        List<ChamadaAlunoResponse> alunos
) {
}

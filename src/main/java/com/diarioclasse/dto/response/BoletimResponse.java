package com.diarioclasse.dto.response;

import java.util.List;

public record BoletimResponse(
        BoletimAlunoInfo aluno,
        List<NotaItemResponse> notas,
        BoletimResumo resumo
) {
    public record BoletimAlunoInfo(
            Integer id,
            String nome,
            String ra,
            String turma
    ) {}

    public record BoletimResumo(
            long totalMaterias,
            long aprovado,
            long reprovado
    ) {}
}

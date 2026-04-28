package com.diarioclasse.dto.response;

public record ChamadaAlunoResponse(
        Integer id,
        Integer idAluno,
        Integer idMateria,
        String nomeAluno,
        Boolean presente,
        String observacao
) {
}

package com.diarioclasse.dto.response;

import java.util.List;

public record BoletimTurmaItemResponse(
        Integer id,
        String nome,
        String ra,
        String turma,
        List<NotaItemResponse> notas
) {}

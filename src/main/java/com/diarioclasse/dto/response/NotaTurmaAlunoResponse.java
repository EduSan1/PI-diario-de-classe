package com.diarioclasse.dto.response;

import java.math.BigDecimal;

public record NotaTurmaAlunoResponse(
        Integer idAluno,
        String nomeAluno,
        String ra,
        Integer notaId,
        BigDecimal notaFinal,
        Boolean aprovado
) {}

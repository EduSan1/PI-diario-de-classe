package com.diarioclasse.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NotaResponse(
        Integer id,
        Integer idAluno,
        String aluno,
        Integer idMateria,
        String materia,
        BigDecimal notaFinal,
        BigDecimal notaDeCorte,
        Boolean aprovado,
        LocalDateTime dataFechamento
) {}

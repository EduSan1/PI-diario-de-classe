package com.diarioclasse.dto.response;

import java.math.BigDecimal;

public record NotaItemResponse(
        Integer id,
        Integer idMateria,
        String materia,
        BigDecimal notaFinal,
        BigDecimal notaDeCorte,
        Boolean aprovado
) {}

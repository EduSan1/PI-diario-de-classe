package com.diarioclasse.dto.response;

import java.math.BigDecimal;

public record NotaItemResponse(
        String materia,
        BigDecimal notaFinal,
        BigDecimal notaDeCorte,
        Boolean aprovado
) {}

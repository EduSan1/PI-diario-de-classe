package com.diarioclasse.dto.response;

public record FrequenciaResponse(
        String materia,
        long totalAulas,
        long presencas,
        long faltas,
        double percentualFrequencia
) {}

package com.diarioclasse.dto.response;

import java.time.LocalDate;

public record PresencaRegistradaResponse(
        int registrados,
        LocalDate data,
        String materia
) {}

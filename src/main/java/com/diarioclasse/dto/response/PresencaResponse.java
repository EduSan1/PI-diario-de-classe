package com.diarioclasse.dto.response;

import java.time.LocalDate;

public record PresencaResponse(
        Integer id,
        Integer idAluno,
        String nomeAluno,
        String materia,
        LocalDate data,
        Boolean presente,
        String observacao
) {}

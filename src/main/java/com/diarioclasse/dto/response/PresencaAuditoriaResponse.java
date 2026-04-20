package com.diarioclasse.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PresencaAuditoriaResponse(
        Integer id,
        Integer idAluno,
        String nomeAluno,
        String materia,
        LocalDate data,
        Boolean presente,
        String observacao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer createdBy,
        Integer updatedBy
) {}

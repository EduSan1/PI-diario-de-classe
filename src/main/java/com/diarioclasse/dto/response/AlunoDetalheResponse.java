package com.diarioclasse.dto.response;

import java.time.LocalDate;

public record AlunoDetalheResponse(
        Integer id,
        String ra,
        String nome,
        LocalDate dataNascimento,
        TurmaResumoResponse turma
) {}

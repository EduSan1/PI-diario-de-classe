package com.diarioclasse.dto.response;

import java.time.LocalDate;

public record AlunoResponse(
        Integer id,
        String ra,
        String nome,
        LocalDate dataNascimento,
        String nomeResponsavel,
        String telefoneResponsavel,
        TurmaResumoResponse turma
) {}

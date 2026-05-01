package com.diarioclasse.dto.request;

import java.time.LocalDate;

public record AtualizarAlunoRequest(
        LocalDate dataNascimento,
        String nomeResponsavel,
        String telefoneResponsavel
) {}

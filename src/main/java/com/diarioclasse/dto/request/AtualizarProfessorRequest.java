package com.diarioclasse.dto.request;

import java.time.LocalDate;
import java.util.List;

public record AtualizarProfessorRequest(
        String formacaoAcademica,
        String especialidadePrincipal,
        LocalDate dataContratacao,
        List<Integer> idsMaterias
) {}

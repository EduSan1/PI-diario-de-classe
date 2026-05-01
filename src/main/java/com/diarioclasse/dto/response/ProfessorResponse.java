package com.diarioclasse.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ProfessorResponse(
        Integer id,
        String nome,
        String registroFuncionario,
        String formacaoAcademica,
        String especialidadePrincipal,
        LocalDate dataContratacao,
        List<MateriaResumoResponse> materias
) {}

package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.ProfessorResumoResponse;
import com.diarioclasse.dto.response.TurmaResponse;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Turma;
import org.springframework.stereotype.Component;

@Component
public class TurmaMapper {

    public TurmaResponse toResponse(Turma turma) {
        Professor prof = turma.getProfessorRegente();
        ProfessorResumoResponse professorResumo = prof == null ? null
                : new ProfessorResumoResponse(prof.getId(), prof.getUsuario().getNome());

        return new TurmaResponse(
                turma.getId(),
                turma.getSerieEscolar(),
                turma.getLetraTurma(),
                turma.getAno(),
                turma.getPeriodo(),
                professorResumo,
                turma.getSalaFisica(),
                turma.getMaxAlunos()
        );
    }
}

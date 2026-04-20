package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.MateriaResumoResponse;
import com.diarioclasse.dto.response.ProfessorResponse;
import com.diarioclasse.model.Professor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfessorMapper {

    public ProfessorResponse toResponse(Professor professor) {
        List<MateriaResumoResponse> materias = professor.getMaterias().stream()
                .map(pm -> new MateriaResumoResponse(pm.getMateria().getId(), pm.getMateria().getNome()))
                .toList();

        return new ProfessorResponse(
                professor.getId(),
                professor.getUsuario().getNome(),
                professor.getRegistroFuncionario(),
                professor.getFormacaoAcademica(),
                professor.getEspecialidadePrincipal(),
                professor.getDataContratacao(),
                materias
        );
    }
}

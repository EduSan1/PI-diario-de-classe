package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarProfessorRequest;
import com.diarioclasse.dto.response.ProfessorResponse;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.ProfessorMapper;
import com.diarioclasse.model.Materia;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.ProfessorMateria;
import com.diarioclasse.model.ProfessorMateriaId;
import com.diarioclasse.repository.MateriaRepository;
import com.diarioclasse.repository.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorMapper professorMapper;

    public ProfessorService(ProfessorRepository professorRepository,
                            MateriaRepository materiaRepository,
                            ProfessorMapper professorMapper) {
        this.professorRepository = professorRepository;
        this.materiaRepository = materiaRepository;
        this.professorMapper = professorMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProfessorResponse> listar(Pageable pageable) {
        return professorRepository.findAllWithMaterias(pageable).map(professorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProfessorResponse buscarPorId(Integer id) {
        return professorMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProfessorResponse atualizar(Integer id, AtualizarProfessorRequest request) {
        Professor professor = buscarEntidade(id);

        if (request.formacaoAcademica() != null) professor.setFormacaoAcademica(request.formacaoAcademica());
        if (request.especialidadePrincipal() != null) professor.setEspecialidadePrincipal(request.especialidadePrincipal());
        if (request.dataContratacao() != null) professor.setDataContratacao(request.dataContratacao());

        if (request.idsMaterias() != null) {
            List<Materia> materias = materiaRepository.findAllById(request.idsMaterias());
            if (materias.size() != request.idsMaterias().size()) {
                throw new RecursoNaoEncontradoException("Uma ou mais matérias informadas não foram encontradas");
            }

            professor.getMaterias().clear();

            List<ProfessorMateria> novasMaterias = materias.stream()
                    .map(m -> {
                        ProfessorMateriaId pmId = new ProfessorMateriaId();
                        pmId.setIdProfessor(professor.getId());
                        pmId.setIdMateria(m.getId());
                        ProfessorMateria pm = new ProfessorMateria();
                        pm.setId(pmId);
                        pm.setProfessor(professor);
                        pm.setMateria(m);
                        return pm;
                    })
                    .toList();

            professor.getMaterias().addAll(novasMaterias);
        }

        professorRepository.save(professor);

        return professorMapper.toResponse(buscarEntidade(id));
    }

    // --- helpers ---

    private Professor buscarEntidade(Integer id) {
        return professorRepository.findByIdWithMaterias(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor com ID " + id + " não encontrado"));
    }
}

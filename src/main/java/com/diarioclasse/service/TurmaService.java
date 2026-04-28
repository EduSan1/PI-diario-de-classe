package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarTurmaRequest;
import com.diarioclasse.dto.request.CriarTurmaRequest;
import com.diarioclasse.dto.response.TurmaMateriaResumoResponse;
import com.diarioclasse.dto.response.TurmaMateriaResponse;
import com.diarioclasse.dto.response.TurmaResponse;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.DadoInvalidoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.TurmaMapper;
import com.diarioclasse.model.Materia;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Turma;
import com.diarioclasse.model.TurmaMateria;
import com.diarioclasse.repository.MateriaRepository;
import com.diarioclasse.repository.ProfessorMateriaRepository;
import com.diarioclasse.repository.ProfessorRepository;
import com.diarioclasse.repository.TurmaMateriaRepository;
import com.diarioclasse.repository.TurmaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final TurmaMateriaRepository turmaMateriaRepository;
    private final ProfessorMateriaRepository professorMateriaRepository;
    private final TurmaMapper mapper;

    public TurmaService(TurmaRepository turmaRepository,
                        ProfessorRepository professorRepository,
                        MateriaRepository materiaRepository,
                        TurmaMateriaRepository turmaMateriaRepository,
                        ProfessorMateriaRepository professorMateriaRepository,
                        TurmaMapper mapper) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.materiaRepository = materiaRepository;
        this.turmaMateriaRepository = turmaMateriaRepository;
        this.professorMateriaRepository = professorMateriaRepository;
        this.mapper = mapper;
    }

    public Page<TurmaResponse> listar(Pageable pageable) {
        return turmaRepository.findAll(pageable).map(mapper::toResponse);
    }

    public TurmaResponse buscarPorId(Integer id) {
        Turma turma = buscarEntidade(id);
        List<TurmaMateriaResumoResponse> materias = turmaMateriaRepository.findAllByTurmaId(id).stream()
            .map(tm -> new TurmaMateriaResumoResponse(
                tm.getMateria().getId(),
                tm.getMateria().getNome(),
                tm.getProfessor().getId(),
                tm.getProfessor().getUsuario().getNome()
            ))
            .toList();
        return mapper.toResponse(turma, materias);
    }

    @Transactional
    public TurmaResponse criar(CriarTurmaRequest request) {
        validarUnicidade(request.serieEscolar(), request.letraTurma(), request.ano(), null);

        Turma turma = new Turma();
        turma.setSerieEscolar(request.serieEscolar());
        turma.setLetraTurma(request.letraTurma());
        turma.setAno(request.ano());
        turma.setPeriodo(request.periodo());
        turma.setSalaFisica(request.salaFisica());
        turma.setMaxAlunos(request.maxAlunos() != null ? request.maxAlunos() : 40);
        turma.setProfessorRegente(resolverProfessor(request.idProfessorRegente()));

        return mapper.toResponse(turmaRepository.save(turma));
    }

    @Transactional
    public TurmaResponse atualizar(Integer id, AtualizarTurmaRequest request) {
        Turma turma = buscarEntidade(id);
        validarUnicidade(request.serieEscolar(), request.letraTurma(), request.ano(), id);

        turma.setSerieEscolar(request.serieEscolar());
        turma.setLetraTurma(request.letraTurma());
        turma.setAno(request.ano());
        turma.setPeriodo(request.periodo());
        turma.setSalaFisica(request.salaFisica());
        turma.setMaxAlunos(request.maxAlunos());
        turma.setProfessorRegente(resolverProfessor(request.idProfessorRegente()));

        return mapper.toResponse(turmaRepository.save(turma));
    }

    @Transactional
    public void remover(Integer id) {
        Turma turma = buscarEntidade(id);
        turmaRepository.delete(turma);
    }

        @Transactional
        public TurmaMateriaResponse atribuirProfessorPorMateria(Integer idTurma, Integer idMateria, Integer idProfessor) {
        Turma turma = buscarEntidade(idTurma);
        Materia materia = materiaRepository.findById(idMateria)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Matéria com ID " + idMateria + " não encontrada"));
        Professor professor = resolverProfessor(idProfessor);

        boolean professorHabilitado = professorMateriaRepository
            .existsByIdIdProfessorAndIdIdMateria(idProfessor, idMateria);
        if (!professorHabilitado) {
            throw new DadoInvalidoException("Professor com ID " + idProfessor
                + " não está habilitado para a matéria de ID " + idMateria);
        }

        TurmaMateria turmaMateria = turmaMateriaRepository.findByTurmaIdAndMateriaId(idTurma, idMateria)
            .orElseGet(TurmaMateria::new);

        if (turmaMateria.getId() == null) {
            turmaMateria.setTurma(turma);
            turmaMateria.setMateria(materia);
        }

        turmaMateria.setProfessor(professor);
        TurmaMateria salvo = turmaMateriaRepository.save(turmaMateria);

        return new TurmaMateriaResponse(
            salvo.getId(),
            turma.getId(),
            materia.getId(),
            materia.getNome(),
            professor.getId(),
            professor.getUsuario().getNome()
        );
        }

    // --- helpers ---

    private Turma buscarEntidade(Integer id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma com ID " + id + " não encontrada"));
    }

    private void validarUnicidade(Integer serie, String letra, Integer ano, Integer idIgnorado) {
        boolean existe = idIgnorado == null
                ? turmaRepository.existsBySerieEscolarAndLetraTurmaAndAno(serie, letra, ano)
                : turmaRepository.existsBySerieEscolarAndLetraTurmaAndAnoAndIdNot(serie, letra, ano, idIgnorado);

        if (existe) {
            throw new ConflitoException(
                    "Já existe uma turma com série " + serie + letra + " no ano " + ano);
        }
    }

    private Professor resolverProfessor(Integer idProfessor) {
        if (idProfessor == null) {
            return null;
        }
        return professorRepository.findById(idProfessor)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor com ID " + idProfessor + " não encontrado"));
    }
}

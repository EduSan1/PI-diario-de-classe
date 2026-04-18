package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarTurmaRequest;
import com.diarioclasse.dto.request.CriarTurmaRequest;
import com.diarioclasse.dto.response.TurmaResponse;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.TurmaMapper;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Turma;
import com.diarioclasse.repository.ProfessorRepository;
import com.diarioclasse.repository.TurmaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaMapper mapper;

    public TurmaService(TurmaRepository turmaRepository,
                        ProfessorRepository professorRepository,
                        TurmaMapper mapper) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.mapper = mapper;
    }

    public Page<TurmaResponse> listar(Pageable pageable) {
        return turmaRepository.findAll(pageable).map(mapper::toResponse);
    }

    public TurmaResponse buscarPorId(Integer id) {
        return mapper.toResponse(buscarEntidade(id));
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

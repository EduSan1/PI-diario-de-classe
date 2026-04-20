package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarAlunoRequest;
import com.diarioclasse.dto.request.VincularTurmaRequest;
import com.diarioclasse.dto.response.AlunoDetalheResponse;
import com.diarioclasse.dto.response.AlunoResponse;
import com.diarioclasse.dto.response.AlunoResumoResponse;
import com.diarioclasse.exception.AcessoNegadoException;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.AlunoMapper;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Turma;
import com.diarioclasse.repository.AlunoRepository;
import com.diarioclasse.repository.ProfessorRepository;
import com.diarioclasse.repository.TurmaRepository;
import com.diarioclasse.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlunoMapper alunoMapper;

    public AlunoService(AlunoRepository alunoRepository,
                        TurmaRepository turmaRepository,
                        ProfessorRepository professorRepository,
                        UsuarioRepository usuarioRepository,
                        AlunoMapper alunoMapper) {
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.usuarioRepository = usuarioRepository;
        this.alunoMapper = alunoMapper;
    }

    @Transactional(readOnly = true)
    public Page<AlunoResponse> listar(Pageable pageable) {
        return alunoRepository.findAll(pageable).map(alunoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AlunoDetalheResponse buscarPorId(Integer id) {
        Aluno aluno = buscarEntidade(id);

        String nomeUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdm = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADM"));

        if (!isAdm) {
            Professor professor = usuarioRepository.findByUsuario(nomeUsuario)
                    .flatMap(u -> professorRepository.findByUsuarioId(u.getId()))
                    .orElseThrow(() -> new AcessoNegadoException("Acesso negado"));

            boolean eRegente = aluno.getTurma() != null
                    && aluno.getTurma().getProfessorRegente() != null
                    && aluno.getTurma().getProfessorRegente().getId().equals(professor.getId());

            if (!eRegente) {
                throw new AcessoNegadoException("Acesso negado — você não é o professor regente da turma deste aluno");
            }
        }

        return alunoMapper.toDetalhe(aluno);
    }

    @Transactional(readOnly = true)
    public List<AlunoResumoResponse> listarPorTurma(Integer idTurma) {
        Turma turma = turmaRepository.findById(idTurma)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma com ID " + idTurma + " não encontrada"));

        String nomeUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdm = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADM"));

        if (!isAdm) {
            Professor professor = usuarioRepository.findByUsuario(nomeUsuario)
                    .flatMap(u -> professorRepository.findByUsuarioId(u.getId()))
                    .orElseThrow(() -> new AcessoNegadoException("Acesso negado"));

            boolean eRegente = turma.getProfessorRegente() != null
                    && turma.getProfessorRegente().getId().equals(professor.getId());

            if (!eRegente) {
                throw new AcessoNegadoException("Acesso negado — você não é o professor regente desta turma");
            }
        }

        return alunoRepository.findByTurmaId(idTurma).stream()
                .map(alunoMapper::toResumo)
                .toList();
    }

    @Transactional
    public AlunoResponse atualizar(Integer id, AtualizarAlunoRequest request) {
        Aluno aluno = buscarEntidade(id);

        if (request.dataNascimento() != null) aluno.setDataNascimento(request.dataNascimento());
        if (request.nomeResponsavel() != null) aluno.setNomeResponsavel(request.nomeResponsavel());
        if (request.telefoneResponsavel() != null) aluno.setTelefoneResponsavel(request.telefoneResponsavel());

        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse vincularTurma(Integer id, VincularTurmaRequest request) {
        Aluno aluno = buscarEntidade(id);

        Turma turma = turmaRepository.findById(request.idTurma())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma com ID " + request.idTurma() + " não encontrada"));

        long atuais = alunoRepository.countByTurmaId(turma.getId());
        if (atuais >= turma.getMaxAlunos()) {
            throw new ConflitoException("Turma já atingiu o limite de " + turma.getMaxAlunos() + " alunos");
        }

        aluno.setTurma(turma);
        return alunoMapper.toResponse(alunoRepository.save(aluno));
    }

    // --- helpers ---

    private Aluno buscarEntidade(Integer id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno com ID " + id + " não encontrado"));
    }
}

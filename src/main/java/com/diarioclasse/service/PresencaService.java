package com.diarioclasse.service;

import com.diarioclasse.dto.request.CorrigirPresencaRequest;
import com.diarioclasse.dto.request.LancarPresencaRequest;
import com.diarioclasse.dto.response.FrequenciaResponse;
import com.diarioclasse.dto.response.PresencaAuditoriaResponse;
import com.diarioclasse.dto.response.PresencaRegistradaResponse;
import com.diarioclasse.dto.response.PresencaResponse;
import com.diarioclasse.exception.AcessoNegadoException;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.DadoInvalidoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.PresencaMapper;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Materia;
import com.diarioclasse.model.Presenca;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Usuario;
import com.diarioclasse.repository.AlunoRepository;
import com.diarioclasse.repository.MateriaRepository;
import com.diarioclasse.repository.PresencaRepository;
import com.diarioclasse.repository.ProfessorRepository;
import com.diarioclasse.repository.TurmaMateriaRepository;
import com.diarioclasse.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AlunoRepository alunoRepository;
    private final MateriaRepository materiaRepository;
    private final TurmaMateriaRepository turmaMateriaRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PresencaMapper presencaMapper;

    public PresencaService(PresencaRepository presencaRepository,
                           AlunoRepository alunoRepository,
                           MateriaRepository materiaRepository,
                           TurmaMateriaRepository turmaMateriaRepository,
                           ProfessorRepository professorRepository,
                           UsuarioRepository usuarioRepository,
                           PresencaMapper presencaMapper) {
        this.presencaRepository = presencaRepository;
        this.alunoRepository = alunoRepository;
        this.materiaRepository = materiaRepository;
        this.turmaMateriaRepository = turmaMateriaRepository;
        this.professorRepository = professorRepository;
        this.usuarioRepository = usuarioRepository;
        this.presencaMapper = presencaMapper;
    }

    // --- POST /presencas ---

    @Transactional
    public PresencaRegistradaResponse lancar(LancarPresencaRequest request) {
        Usuario usuarioAtual = usuarioAtual();
        boolean isAdm = isAdm();

        Materia materia = materiaRepository.findById(request.idMateria())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matéria com ID " + request.idMateria() + " não encontrada"));

        if (!isAdm) {
            Professor professor = professorAtual(usuarioAtual);
            validarAcessoProfessorAMateria(professor, request.idMateria(), request.presencas().stream()
                    .map(i -> alunoRepository.findById(i.idAluno())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno com ID " + i.idAluno() + " não encontrado")))
                    .toList());
        }

        List<Presenca> novas = new ArrayList<>();
        for (var item : request.presencas()) {
            Aluno aluno = alunoRepository.findById(item.idAluno())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno com ID " + item.idAluno() + " não encontrado"));

            if (presencaRepository.existsByAlunoIdAndMateriaIdAndData(aluno.getId(), materia.getId(), request.data())) {
                throw new ConflitoException("Presença já registrada para o aluno " + aluno.getUsuario().getNome()
                        + " na matéria " + materia.getNome() + " em " + request.data());
            }

            Presenca p = new Presenca();
            p.setAluno(aluno);
            p.setMateria(materia);
            p.setData(request.data());
            p.setPresente(item.presente());
            p.setObservacao(item.observacao());
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            p.setCreatedBy(usuarioAtual.getId());
            p.setUpdatedBy(usuarioAtual.getId());
            novas.add(p);
        }

        presencaRepository.saveAll(novas);
        return new PresencaRegistradaResponse(novas.size(), request.data(), materia.getNome());
    }

    // --- GET /presencas/aluno/{idAluno} ---

    @Transactional(readOnly = true)
    public List<PresencaResponse> listarPorAluno(Integer idAluno) {
        Aluno aluno = buscarAluno(idAluno);
        if (!isAdm()) {
            validarAcessoProfessorAAluno(professorAtual(usuarioAtual()), aluno);
        }
        return presencaRepository.findByAlunoId(idAluno).stream()
                .map(presencaMapper::toResponse).toList();
    }

    // --- GET /presencas/aluno/{idAluno}/materia/{idMateria} ---

    @Transactional(readOnly = true)
    public List<PresencaResponse> listarPorAlunoEMateria(Integer idAluno, Integer idMateria) {
        Aluno aluno = buscarAluno(idAluno);
        if (!isAdm()) {
            validarAcessoProfessorAAluno(professorAtual(usuarioAtual()), aluno);
        }
        return presencaRepository.findByAlunoIdAndMateriaId(idAluno, idMateria).stream()
                .map(presencaMapper::toResponse).toList();
    }

    // --- GET /presencas/turma/{idTurma}/data/{data} ---

    @Transactional(readOnly = true)
    public List<PresencaResponse> listarPorTurmaEData(Integer idTurma, LocalDate data) {
        if (!isAdm()) {
            Professor professor = professorAtual(usuarioAtual());
            boolean temAcesso = turmaMateriaRepository
                    .existsByTurmaIdAndMateriaIdAndProfessorId(idTurma, null, professor.getId());
            // Verificação mais ampla: professor leciona qualquer matéria nesta turma
            boolean lecionaNaTurma = turmaMateriaRepository.findAll().stream()
                    .anyMatch(tm -> tm.getTurma().getId().equals(idTurma)
                            && tm.getProfessor().getId().equals(professor.getId()));
            if (!lecionaNaTurma) {
                throw new AcessoNegadoException("Acesso negado — você não leciona nesta turma");
            }
        }
        return presencaRepository.findByTurmaIdAndData(idTurma, data).stream()
                .map(presencaMapper::toResponse).toList();
    }

    // --- GET /presencas/me ---

    @Transactional(readOnly = true)
    public List<FrequenciaResponse> minhaFrequencia() {
        Usuario usuario = usuarioAtual();
        List<Presenca> presencas = presencaRepository.findByUsuarioId(usuario.getId());
        return presencaMapper.toFrequencia(presencas);
    }

    // --- PUT /presencas/{id} ---

    @Transactional
    public PresencaResponse corrigir(Integer id, CorrigirPresencaRequest request) {
        Presenca presenca = buscarPresenca(id);
        Usuario usuarioAtual = usuarioAtual();
        boolean isAdm = isAdm();

        if (!isAdm) {
            Professor professor = professorAtual(usuarioAtual);
            validarAcessoProfessorAAluno(professor, presenca.getAluno());

            LocalDate hoje = LocalDate.now();
            LocalDate ontem = hoje.minusDays(1);
            if (presenca.getData().isBefore(ontem)) {
                throw new AcessoNegadoException(
                        "Professores só podem corrigir presenças do dia atual ou do dia anterior. "
                        + "Para alterações em datas anteriores, solicite a um ADM.");
            }
        }

        presenca.setPresente(request.presente());
        presenca.setObservacao(request.observacao());
        presenca.setUpdatedAt(LocalDateTime.now());
        presenca.setUpdatedBy(usuarioAtual.getId());

        return presencaMapper.toResponse(presencaRepository.save(presenca));
    }

    // --- GET /presencas/{id}/auditoria ---

    @Transactional(readOnly = true)
    public PresencaAuditoriaResponse auditoria(Integer id) {
        return presencaMapper.toAuditoria(buscarPresenca(id));
    }

    // --- helpers ---

    private boolean isAdm() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADM"));
    }

    private Usuario usuarioAtual() {
        String nome = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsuario(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));
    }

    private Professor professorAtual(Usuario usuario) {
        return professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Acesso negado"));
    }

    private Aluno buscarAluno(Integer id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno com ID " + id + " não encontrado"));
    }

    private Presenca buscarPresenca(Integer id) {
        return presencaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Presença com ID " + id + " não encontrada"));
    }

    private void validarAcessoProfessorAAluno(Professor professor, Aluno aluno) {
        if (aluno.getTurma() == null) {
            throw new AcessoNegadoException("Acesso negado — aluno não está vinculado a uma turma");
        }
        boolean lecionaNaTurma = turmaMateriaRepository.findAll().stream()
                .anyMatch(tm -> tm.getTurma().getId().equals(aluno.getTurma().getId())
                        && tm.getProfessor().getId().equals(professor.getId()));
        if (!lecionaNaTurma) {
            throw new AcessoNegadoException("Acesso negado — você não leciona na turma deste aluno");
        }
    }

    private void validarAcessoProfessorAMateria(Professor professor, Integer idMateria, List<Aluno> alunos) {
        for (Aluno aluno : alunos) {
            if (aluno.getTurma() == null) {
                throw new DadoInvalidoException("Aluno " + aluno.getUsuario().getNome() + " não está vinculado a uma turma");
            }
            boolean temAcesso = turmaMateriaRepository.existsByTurmaIdAndMateriaIdAndProfessorId(
                    aluno.getTurma().getId(), idMateria, professor.getId());
            if (!temAcesso) {
                throw new AcessoNegadoException("Acesso negado — você não leciona a matéria solicitada na turma do aluno "
                        + aluno.getUsuario().getNome());
            }
        }
    }
}

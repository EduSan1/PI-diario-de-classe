package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarNotaRequest;
import com.diarioclasse.dto.request.LancarNotaRequest;
import com.diarioclasse.dto.response.BoletimResponse;
import com.diarioclasse.dto.response.BoletimTurmaItemResponse;
import com.diarioclasse.dto.response.NotaResponse;
import com.diarioclasse.dto.response.NotaTurmaAlunoResponse;
import com.diarioclasse.exception.AcessoNegadoException;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.NotaMapper;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Materia;
import com.diarioclasse.model.Nota;
import com.diarioclasse.model.Professor;
import com.diarioclasse.model.Usuario;
import com.diarioclasse.repository.AlunoRepository;
import com.diarioclasse.repository.MateriaRepository;
import com.diarioclasse.repository.NotaRepository;
import com.diarioclasse.repository.ProfessorRepository;
import com.diarioclasse.repository.TurmaRepository;
import com.diarioclasse.repository.TurmaMateriaRepository;
import com.diarioclasse.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final TurmaMateriaRepository turmaMateriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotaMapper notaMapper;

    public NotaService(NotaRepository notaRepository,
                       AlunoRepository alunoRepository,
                       MateriaRepository materiaRepository,
                       ProfessorRepository professorRepository,
                       TurmaRepository turmaRepository,
                       TurmaMateriaRepository turmaMateriaRepository,
                       UsuarioRepository usuarioRepository,
                       NotaMapper notaMapper) {
        this.notaRepository = notaRepository;
        this.alunoRepository = alunoRepository;
        this.materiaRepository = materiaRepository;
        this.professorRepository = professorRepository;
        this.turmaRepository = turmaRepository;
        this.turmaMateriaRepository = turmaMateriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notaMapper = notaMapper;
    }

    // --- POST /notas ---

    @Transactional
    public NotaResponse lancar(LancarNotaRequest request) {
        Aluno aluno = buscarAluno(request.idAluno());
        Materia materia = buscarMateria(request.idMateria());

        if (!isAdm()) {
            validarAcessoProfessorAMateriaDoAluno(professorAtual(), aluno, request.idMateria());
        }

        if (notaRepository.existsByAlunoIdAndMateriaId(aluno.getId(), materia.getId())) {
            throw new ConflitoException("Nota já lançada para o aluno " + aluno.getUsuario().getNome()
                    + " na matéria " + materia.getNome() + ". Use PUT para atualizar.",
                    List.of("idAluno", "idMateria"));
        }

        boolean aprovado = request.notaFinal().compareTo(BigDecimal.valueOf(materia.getNotaDeCorte())) >= 0;

        Nota nota = new Nota();
        nota.setAluno(aluno);
        nota.setMateria(materia);
        nota.setNotaFinal(request.notaFinal());
        nota.setAprovado(aprovado);
        nota.setDataFechamento(LocalDateTime.now());

        return notaMapper.toResponse(notaRepository.save(nota));
    }

    // --- PUT /notas/{id} ---

    @Transactional
    public NotaResponse atualizar(Integer id, AtualizarNotaRequest request) {
        Nota nota = buscarNota(id);

        if (!isAdm()) {
            validarAcessoProfessorAMateriaDoAluno(professorAtual(), nota.getAluno(), nota.getMateria().getId());
        }

        boolean aprovado = request.notaFinal().compareTo(BigDecimal.valueOf(nota.getMateria().getNotaDeCorte())) >= 0;
        nota.setNotaFinal(request.notaFinal());
        nota.setAprovado(aprovado);
        nota.setDataFechamento(LocalDateTime.now());

        return notaMapper.toResponse(notaRepository.save(nota));
    }

    // --- GET /notas/aluno/{idAluno} ---

    @Transactional(readOnly = true)
    public BoletimResponse boletimPorAluno(Integer idAluno) {
        Aluno aluno = buscarAluno(idAluno);
        List<Nota> notas = notaRepository.findByAlunoId(idAluno);
        return notaMapper.toBoletim(aluno, notas);
    }

    // --- GET /notas/turma/{idTurma} ---

    @Transactional(readOnly = true)
    public List<BoletimTurmaItemResponse> notasPorTurma(Integer idTurma) {
        if (!turmaRepository.existsById(idTurma)) {
            throw new RecursoNaoEncontradoException("Turma com ID " + idTurma + " não encontrada");
        }
        List<Nota> notas = notaRepository.findByTurmaId(idTurma);
        return notaMapper.toBoletimTurma(notas);
    }

    // --- GET /notas/me ---

    @Transactional(readOnly = true)
    public BoletimResponse meuBoletim() {
        Usuario usuario = usuarioAtual();
        Aluno aluno = alunoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado para o usuário autenticado"));
        List<Nota> notas = notaRepository.findByAlunoId(aluno.getId());
        return notaMapper.toBoletim(aluno, notas);
    }

    // --- helpers ---

    private boolean isAdm() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADM"));
    }

    private Usuario usuarioAtual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));
    }

    private Professor professorAtual() {
        Usuario usuario = usuarioAtual();
        return professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Acesso negado"));
    }

    private Aluno buscarAluno(Integer id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno com ID " + id + " não encontrado"));
    }

    private Materia buscarMateria(Integer id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matéria com ID " + id + " não encontrada"));
    }

    private Nota buscarNota(Integer id) {
        return notaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nota com ID " + id + " não encontrada"));
    }

    // --- GET /notas/turma/{idTurma}/materia/{idMateria} ---

    @Transactional(readOnly = true)
    public List<NotaTurmaAlunoResponse> listarPorTurmaEMateria(Integer idTurma, Integer idMateria) {
        if (!turmaRepository.existsById(idTurma)) {
            throw new RecursoNaoEncontradoException("Turma com ID " + idTurma + " não encontrada");
        }
        if (!materiaRepository.existsById(idMateria)) {
            throw new RecursoNaoEncontradoException("Matéria com ID " + idMateria + " não encontrada");
        }

        List<Aluno> alunos = alunoRepository.findByTurmaId(idTurma);
        List<Nota> notas = notaRepository.findByTurmaIdAndMateriaId(idTurma, idMateria);

        return alunos.stream()
                .map(aluno -> {
                    Nota nota = notas.stream()
                            .filter(n -> n.getAluno().getId().equals(aluno.getId()))
                            .findFirst()
                            .orElse(null);
                    return new NotaTurmaAlunoResponse(
                            aluno.getId(),
                            aluno.getUsuario().getNome(),
                            aluno.getRa(),
                            nota != null ? nota.getId() : null,
                            nota != null ? nota.getNotaFinal() : null,
                            nota != null ? nota.getAprovado() : null
                    );
                })
                .toList();
    }

    private void validarAcessoProfessorAMateriaDoAluno(Professor professor, Aluno aluno, Integer idMateria) {
        if (aluno.getTurma() == null) {
            throw new AcessoNegadoException("Acesso negado — aluno não está vinculado a uma turma");
        }
        boolean temAcesso = turmaMateriaRepository.existsByTurmaIdAndMateriaIdAndProfessorId(
                aluno.getTurma().getId(), idMateria, professor.getId());
        if (!temAcesso) {
            throw new AcessoNegadoException("Acesso negado — você não é o professor responsável por esta matéria na turma do aluno");
        }
    }
}

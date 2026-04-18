package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarUsuarioRequest;
import com.diarioclasse.dto.request.CriarUsuarioRequest;
import com.diarioclasse.dto.response.UsuarioResponse;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.DadoInvalidoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.UsuarioMapper;
import com.diarioclasse.model.*;
import com.diarioclasse.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          AdministradorRepository administradorRepository,
                          ProfessorRepository professorRepository,
                          AlunoRepository alunoRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public Page<UsuarioResponse> listar(Pageable pageable, String status) {
        return switch (status == null ? "ativos" : status.toLowerCase()) {
            case "todos" -> usuarioRepository.findAll(pageable).map(mapper::toResponse);
            case "inativos" -> usuarioRepository.findAllByAtivo(false, pageable).map(mapper::toResponse);
            default -> usuarioRepository.findAllByAtivo(true, pageable).map(mapper::toResponse);
        };
    }

    public UsuarioResponse buscarPorId(Integer id) {
        return mapper.toResponse(buscarOuLancar(id));
    }

    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest request) {
        if (usuarioRepository.existsByUsuario(request.usuario().toLowerCase())) {
            throw new ConflitoException("Usuário '" + request.usuario() + "' já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setUsuario(request.usuario().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setTipoUsuario(request.tipoUsuario());
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setCriadoPorAdm(getIdAdmAutenticado());

        usuarioRepository.save(usuario);

        switch (request.tipoUsuario()) {
            case ADM -> criarAdministrador(usuario, request);
            case PROFESSOR -> criarProfessor(usuario, request);
            case ALUNO -> criarAluno(usuario, request);
        }

        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Integer id, AtualizarUsuarioRequest request) {
        Usuario usuario = buscarOuLancar(id);

        if (!usuario.getUsuario().equals(request.usuario().toLowerCase())
                && usuarioRepository.existsByUsuario(request.usuario().toLowerCase())) {
            throw new ConflitoException("Usuário '" + request.usuario() + "' já existe");
        }

        usuario.setNome(request.nome());
        usuario.setUsuario(request.usuario().toLowerCase());

        return mapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desativar(Integer id) {
        Usuario usuario = buscarOuLancar(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    private void criarAdministrador(Usuario usuario, CriarUsuarioRequest request) {
        if (request.dadosAdministrador() == null) {
            throw new DadoInvalidoException("dadosAdministrador é obrigatório para o tipo ADM");
        }
        Administrador adm = new Administrador();
        adm.setUsuario(usuario);
        adm.setDepartamento(request.dadosAdministrador().departamento());
        adm.setContatoEmergencia(request.dadosAdministrador().contatoEmergencia());
        administradorRepository.save(adm);
    }

    private void criarProfessor(Usuario usuario, CriarUsuarioRequest request) {
        if (request.dadosProfessor() == null) {
            throw new DadoInvalidoException("dadosProfessor é obrigatório para o tipo PROFESSOR");
        }
        Professor professor = new Professor();
        professor.setUsuario(usuario);
        professor.setRegistroFuncionario(request.dadosProfessor().registroFuncionario());
        professor.setFormacaoAcademica(request.dadosProfessor().formacaoAcademica());
        professor.setEspecialidadePrincipal(request.dadosProfessor().especialidadePrincipal());
        professorRepository.save(professor);
    }

    private void criarAluno(Usuario usuario, CriarUsuarioRequest request) {
        if (request.dadosAluno() == null) {
            throw new DadoInvalidoException("dadosAluno é obrigatório para o tipo ALUNO");
        }
        Aluno aluno = new Aluno();
        aluno.setUsuario(usuario);
        aluno.setRa(request.dadosAluno().ra());
        aluno.setNomeResponsavel(request.dadosAluno().nomeResponsavel());
        aluno.setTelefoneResponsavel(request.dadosAluno().telefoneResponsavel());
        alunoRepository.save(aluno);
    }

    private Usuario buscarOuLancar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado"));
    }

    private Integer getIdAdmAutenticado() {
        String nomeUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsuario(nomeUsuario)
                .map(Usuario::getId)
                .orElse(null);
    }
}

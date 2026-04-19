package com.diarioclasse.service;

import com.diarioclasse.dto.request.AtualizarMateriaRequest;
import com.diarioclasse.dto.request.CriarMateriaRequest;
import com.diarioclasse.dto.response.MateriaResponse;
import com.diarioclasse.exception.ConflitoException;
import com.diarioclasse.exception.RecursoNaoEncontradoException;
import com.diarioclasse.mapper.MateriaMapper;
import com.diarioclasse.model.Materia;
import com.diarioclasse.repository.MateriaRepository;
import com.diarioclasse.repository.ProfessorMateriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final ProfessorMateriaRepository professorMateriaRepository;
    private final MateriaMapper mapper;

    public MateriaService(MateriaRepository materiaRepository,
                          ProfessorMateriaRepository professorMateriaRepository,
                          MateriaMapper mapper) {
        this.materiaRepository = materiaRepository;
        this.professorMateriaRepository = professorMateriaRepository;
        this.mapper = mapper;
    }

    public Page<MateriaResponse> listar(Pageable pageable) {
        return materiaRepository.findAll(pageable).map(mapper::toResponse);
    }

    public MateriaResponse buscarPorId(Integer id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public MateriaResponse criar(CriarMateriaRequest request) {
        if (materiaRepository.existsByNome(request.nome())) {
            throw new ConflitoException("Já existe uma matéria com o nome '" + request.nome() + "'");
        }

        Materia materia = new Materia();
        materia.setNome(request.nome());
        materia.setCargaHoraria(request.cargaHoraria());
        materia.setNotaDeCorte(request.notaDeCorte());
        materia.setObrigatoria(request.obrigatoria() != null ? request.obrigatoria() : true);

        return mapper.toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public MateriaResponse atualizar(Integer id, AtualizarMateriaRequest request) {
        Materia materia = buscarEntidade(id);

        if (materiaRepository.existsByNomeAndIdNot(request.nome(), id)) {
            throw new ConflitoException("Já existe uma matéria com o nome '" + request.nome() + "'");
        }

        materia.setNome(request.nome());
        materia.setCargaHoraria(request.cargaHoraria());
        materia.setNotaDeCorte(request.notaDeCorte());
        if (request.obrigatoria() != null) {
            materia.setObrigatoria(request.obrigatoria());
        }

        return mapper.toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public void remover(Integer id) {
        Materia materia = buscarEntidade(id);

        if (professorMateriaRepository.existsByIdIdMateria(id)) {
            throw new ConflitoException("Não é possível remover a matéria pois ela está vinculada a professores");
        }

        materiaRepository.delete(materia);
    }

    // --- helpers ---

    private Materia buscarEntidade(Integer id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matéria com ID " + id + " não encontrada"));
    }
}

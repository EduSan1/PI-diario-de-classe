package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.AlunoDetalheResponse;
import com.diarioclasse.dto.response.AlunoResponse;
import com.diarioclasse.dto.response.AlunoResumoResponse;
import com.diarioclasse.dto.response.TurmaResumoResponse;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Turma;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapper {

    public AlunoResponse toResponse(Aluno aluno) {
        TurmaResumoResponse turmaResumo = null;
        if (aluno.getTurma() != null) {
            Turma t = aluno.getTurma();
            String descricao = t.getSerieEscolar() + t.getLetraTurma() + " - " + t.getAno();
            turmaResumo = new TurmaResumoResponse(t.getId(), descricao);
        }

        return new AlunoResponse(
                aluno.getId(),
                aluno.getRa(),
                aluno.getUsuario().getNome(),
                aluno.getDataNascimento(),
                aluno.getNomeResponsavel(),
                aluno.getTelefoneResponsavel(),
                turmaResumo
        );
    }

    public AlunoResumoResponse toResumo(Aluno aluno) {
        return new AlunoResumoResponse(aluno.getId(), aluno.getUsuario().getNome());
    }

    public AlunoDetalheResponse toDetalhe(Aluno aluno) {
        TurmaResumoResponse turmaResumo = null;
        if (aluno.getTurma() != null) {
            Turma t = aluno.getTurma();
            String descricao = t.getSerieEscolar() + t.getLetraTurma() + " - " + t.getAno();
            turmaResumo = new TurmaResumoResponse(t.getId(), descricao);
        }
        return new AlunoDetalheResponse(
                aluno.getId(),
                aluno.getRa(),
                aluno.getUsuario().getNome(),
                aluno.getDataNascimento(),
                turmaResumo
        );
    }
}

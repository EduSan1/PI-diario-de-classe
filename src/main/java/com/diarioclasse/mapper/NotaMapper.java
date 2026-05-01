package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.BoletimResponse;
import com.diarioclasse.dto.response.NotaItemResponse;
import com.diarioclasse.dto.response.NotaResponse;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Nota;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class NotaMapper {

    public NotaResponse toResponse(Nota nota) {
        return new NotaResponse(
                nota.getId(),
                nota.getAluno().getId(),
                nota.getAluno().getUsuario().getNome(),
                nota.getMateria().getId(),
                nota.getMateria().getNome(),
                nota.getNotaFinal(),
                BigDecimal.valueOf(nota.getMateria().getNotaDeCorte()),
                nota.getAprovado(),
                nota.getDataFechamento()
        );
    }

    public NotaItemResponse toItemResponse(Nota nota) {
        return new NotaItemResponse(
                nota.getMateria().getNome(),
                nota.getNotaFinal(),
                BigDecimal.valueOf(nota.getMateria().getNotaDeCorte()),
                nota.getAprovado()
        );
    }

    public BoletimResponse toBoletim(Aluno aluno, List<Nota> notas) {
        String nomeTurma = aluno.getTurma() != null
                ? aluno.getTurma().getSerieEscolar() + aluno.getTurma().getLetraTurma() + " - " + aluno.getTurma().getAno()
                : null;

        List<NotaItemResponse> itens = notas.stream().map(this::toItemResponse).toList();

        long aprovados = notas.stream().filter(Nota::getAprovado).count();
        long reprovados = notas.size() - aprovados;

        return new BoletimResponse(
                new BoletimResponse.BoletimAlunoInfo(
                        aluno.getId(),
                        aluno.getUsuario().getNome(),
                        aluno.getRa(),
                        nomeTurma
                ),
                itens,
                new BoletimResponse.BoletimResumo(notas.size(), aprovados, reprovados)
        );
    }
}

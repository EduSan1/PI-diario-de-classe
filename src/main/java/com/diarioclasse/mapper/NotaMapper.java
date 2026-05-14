package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.BoletimResponse;
import com.diarioclasse.dto.response.BoletimTurmaItemResponse;
import com.diarioclasse.dto.response.NotaItemResponse;
import com.diarioclasse.dto.response.NotaResponse;
import com.diarioclasse.model.Aluno;
import com.diarioclasse.model.Nota;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                nota.getId(),
                nota.getMateria().getId(),
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

    public List<BoletimTurmaItemResponse> toBoletimTurma(List<Nota> notas) {
        Map<Aluno, List<Nota>> porAluno = notas.stream()
                .collect(Collectors.groupingBy(Nota::getAluno));

        return porAluno.entrySet().stream()
                .map(entry -> {
                    Aluno aluno = entry.getKey();
                    String nomeTurma = aluno.getTurma() != null
                            ? aluno.getTurma().getSerieEscolar() + aluno.getTurma().getLetraTurma() + " - " + aluno.getTurma().getAno()
                            : null;
                    List<NotaItemResponse> itens = entry.getValue().stream()
                            .map(this::toItemResponse).toList();
                    return new BoletimTurmaItemResponse(
                            aluno.getId(),
                            aluno.getUsuario().getNome(),
                            aluno.getRa(),
                            nomeTurma,
                            itens
                    );
                })
                .sorted(Comparator.comparing(BoletimTurmaItemResponse::id))
                .toList();
    }
}

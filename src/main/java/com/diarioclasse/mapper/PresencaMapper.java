package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.FrequenciaResponse;
import com.diarioclasse.dto.response.PresencaAuditoriaResponse;
import com.diarioclasse.dto.response.PresencaResponse;
import com.diarioclasse.model.Presenca;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PresencaMapper {

    public PresencaResponse toResponse(Presenca p) {
        return new PresencaResponse(
                p.getId(),
                p.getAluno().getId(),
                p.getAluno().getUsuario().getNome(),
                p.getMateria().getNome(),
                p.getData(),
                p.getPresente(),
                p.getObservacao()
        );
    }

    public PresencaAuditoriaResponse toAuditoria(Presenca p) {
        return new PresencaAuditoriaResponse(
                p.getId(),
                p.getAluno().getId(),
                p.getAluno().getUsuario().getNome(),
                p.getMateria().getNome(),
                p.getData(),
                p.getPresente(),
                p.getObservacao(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getCreatedBy(),
                p.getUpdatedBy()
        );
    }

    public List<FrequenciaResponse> toFrequencia(List<Presenca> presencas) {
        Map<String, List<Presenca>> porMateria = presencas.stream()
                .collect(Collectors.groupingBy(p -> p.getMateria().getNome()));

        return porMateria.entrySet().stream().map(entry -> {
            String materia = entry.getKey();
            List<Presenca> lista = entry.getValue();
            long total = lista.size();
            long presentes = lista.stream().filter(Presenca::getPresente).count();
            long faltas = total - presentes;
            double percentual = total == 0 ? 0.0 : Math.round((presentes * 100.0 / total) * 10.0) / 10.0;
            return new FrequenciaResponse(materia, total, presentes, faltas, percentual);
        }).toList();
    }
}

package com.diarioclasse.mapper;

import com.diarioclasse.dto.response.MateriaResponse;
import com.diarioclasse.model.Materia;
import org.springframework.stereotype.Component;

@Component
public class MateriaMapper {

    public MateriaResponse toResponse(Materia materia) {
        return new MateriaResponse(
                materia.getId(),
                materia.getNome(),
                materia.getCargaHoraria(),
                materia.getNotaDeCorte(),
                materia.getObrigatoria()
        );
    }
}

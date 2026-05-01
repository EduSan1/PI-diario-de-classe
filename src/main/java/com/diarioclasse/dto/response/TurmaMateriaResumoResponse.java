package com.diarioclasse.dto.response;

public record TurmaMateriaResumoResponse(
        Integer idMateria,
        String materia,
        Integer idProfessor,
        String professor
) {
}

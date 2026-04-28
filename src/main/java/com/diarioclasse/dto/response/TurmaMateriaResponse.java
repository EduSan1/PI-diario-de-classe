package com.diarioclasse.dto.response;

public record TurmaMateriaResponse(
        Integer id,
        Integer idTurma,
        Integer idMateria,
        String materia,
        Integer idProfessor,
        String professor
) {
}

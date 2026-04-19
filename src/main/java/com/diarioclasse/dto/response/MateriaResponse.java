package com.diarioclasse.dto.response;

public record MateriaResponse(
        Integer id,
        String nome,
        Integer cargaHoraria,
        Integer notaDeCorte,
        Boolean obrigatoria
) {}

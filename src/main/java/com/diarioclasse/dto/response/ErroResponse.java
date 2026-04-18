package com.diarioclasse.dto.response;

public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        String caminho
) {}

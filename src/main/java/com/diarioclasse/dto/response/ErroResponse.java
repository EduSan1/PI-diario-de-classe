package com.diarioclasse.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> campos
) {
    public ErroResponse(int status, String erro, String mensagem, String caminho) {
        this(status, erro, mensagem, caminho, null);
    }
}

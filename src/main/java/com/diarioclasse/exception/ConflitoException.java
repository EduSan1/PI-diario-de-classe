package com.diarioclasse.exception;

import java.util.List;

public class ConflitoException extends RuntimeException {

    private final List<String> campos;

    public ConflitoException(String mensagem) {
        super(mensagem);
        this.campos = null;
    }

    public ConflitoException(String mensagem, List<String> campos) {
        super(mensagem);
        this.campos = campos;
    }

    public List<String> getCampos() {
        return campos;
    }
}

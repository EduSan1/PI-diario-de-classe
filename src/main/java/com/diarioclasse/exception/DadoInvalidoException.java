package com.diarioclasse.exception;

public class DadoInvalidoException extends RuntimeException {
    public DadoInvalidoException( String mensagem) {
        super(mensagem);
    }
}

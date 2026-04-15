package com.diarioclasse.exception;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException( String mensagem) {
        super(mensagem);
    }
}

package br.com.gestaopsicologica.exceptions;

public class PagamentoPendenteException extends RuntimeException {
    public PagamentoPendenteException(String message) {
        super(message);
    }
}

package br.com.gestaopsicologica.exceptions;

public class StatusAtendimentoInvalidoException extends RuntimeException {
    public StatusAtendimentoInvalidoException(String message) {
        super(message);
    }
}

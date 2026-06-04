package br.com.gestaopsicologica.exceptions;

public class CriptografiaException extends RuntimeException {
    public CriptografiaException(String message) {
        super(message);
    }

    public CriptografiaException(String message, Throwable cause) { super(message, cause);}
}

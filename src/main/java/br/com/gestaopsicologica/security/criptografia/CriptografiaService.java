package br.com.gestaopsicologica.security.criptografia;

public interface CriptografiaService {
    String encriptografa(String texto);

    String descriptografa(String texto);
}

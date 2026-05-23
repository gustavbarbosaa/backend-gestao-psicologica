package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.*;

public record CadastroRequest(
        @NotBlank(message = "O nome é obrigatório!")
        String nome,
        @NotBlank @Email(message = "O email precisa ser válido!")
        String email,
        @Size(min = 6, message = "A senha deve possuir no mínimo  6 caracteres!")
        @NotBlank(message = "A senha é obrigatória!")
        String senha) {

}

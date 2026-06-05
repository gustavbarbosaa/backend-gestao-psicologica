package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "Token do Google obrigatório")
        String idToken
) {
}

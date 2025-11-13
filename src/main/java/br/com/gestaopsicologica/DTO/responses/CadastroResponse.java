package br.com.gestaopsicologica.DTO.responses;

import java.util.UUID;

public record CadastroResponse(UUID id, String email, String nome) {
}

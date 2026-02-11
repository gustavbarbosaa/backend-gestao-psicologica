package br.com.gestaopsicologica.DTO.responses;

import java.util.UUID;

public record PacienteMinResponse(UUID id, String nome, Boolean ativo) {
}

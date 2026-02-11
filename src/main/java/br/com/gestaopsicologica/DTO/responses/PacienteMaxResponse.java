package br.com.gestaopsicologica.DTO.responses;

import java.util.UUID;

public record PacienteMaxResponse(UUID id, String nome, String email, String telefone, Boolean ativo) {
}

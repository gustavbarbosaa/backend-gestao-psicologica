package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.NotNull;

public record PacienteRequest(@NotNull String nome, String email, @NotNull String telefone) {
}

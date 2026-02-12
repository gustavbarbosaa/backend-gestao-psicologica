package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PacienteRequest(@NotNull String nome, String email, String telefone, UUID profissionalId) {
}

package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PacienteRequest(@NotNull String nome, String email, String telefone, BigDecimal valorSessaoPadrao) {
}

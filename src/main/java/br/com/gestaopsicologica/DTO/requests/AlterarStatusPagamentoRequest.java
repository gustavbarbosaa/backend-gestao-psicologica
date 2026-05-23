package br.com.gestaopsicologica.DTO.requests;

import br.com.gestaopsicologica.enums.StatusPagamento;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusPagamentoRequest(@NotNull StatusPagamento statusPagamento) {
}

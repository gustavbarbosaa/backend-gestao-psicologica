package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EvolucaoPsicoterapicaRequest(
        @NotNull(message = "O agendamento é obrigatório")
        UUID agendamentoId,
        @NotNull(message = "Necessário informar o conteúdo")
        String conteudo,
        String observacoes
) {
}

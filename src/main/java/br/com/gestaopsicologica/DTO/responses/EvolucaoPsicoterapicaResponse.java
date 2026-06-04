package br.com.gestaopsicologica.DTO.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvolucaoPsicoterapicaResponse(
        UUID id,
        AgendamentoResponse agendamento,
        String conteudo,
        String observacoes,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAlteracao
) {
}

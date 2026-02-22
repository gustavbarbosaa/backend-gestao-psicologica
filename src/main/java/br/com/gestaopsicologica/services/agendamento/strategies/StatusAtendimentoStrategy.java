package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;

public interface StatusAtendimentoStrategy {
    StatusAtendimento getStatus();

    AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus);
}

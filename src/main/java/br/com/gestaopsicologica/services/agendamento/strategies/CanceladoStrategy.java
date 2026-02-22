package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class CanceladoStrategy implements StatusAtendimentoStrategy {

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.CANCELADO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        throw new StatusAtendimentoInvalidoException(
                "Atendimento CANCELADO não pode ter seu status alterado"
        );
    }
}

package br.com.gestaopsicologica.services.agendamento.states;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class CanceladoState implements StatusAtendimentoState {

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

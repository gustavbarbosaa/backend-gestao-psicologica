package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class ConcluidoStrategy implements StatusAtendimentoStrategy {
    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.CONCLUIDO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {

        throw new StatusAtendimentoInvalidoException(
                "Atendimento CONCLUÍDO não pode ter seu status alterado"
        );
    }
}

package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.services.agendamento.helpers.AgendamentoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmAndamentoStrategy implements StatusAtendimentoStrategy {
    private final AgendamentoHelper agendamentoHelper;

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.EM_ANDAMENTO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        switch (novoStatus) {
            case CONCLUIDO -> {
                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            default ->
                    throw new StatusAtendimentoInvalidoException(
                            "EM_ANDAMENTO → " + novoStatus + " não permitido"
                    );
        }
    }
}

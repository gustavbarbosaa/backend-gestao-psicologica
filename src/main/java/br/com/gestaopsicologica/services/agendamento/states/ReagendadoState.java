package br.com.gestaopsicologica.services.agendamento.states;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.services.agendamento.helpers.AgendamentoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReagendadoState implements StatusAtendimentoState {
    private final AgendamentoHelper agendamentoHelper;

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.REAGENDADO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        switch (novoStatus) {
            case CRIADO, CONFIRMADO -> {
                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            default -> throw new StatusAtendimentoInvalidoException("REAGENDADO → " + novoStatus + " não permitido");
        }
    }
}

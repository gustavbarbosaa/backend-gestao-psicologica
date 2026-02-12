package br.com.gestaopsicologica.services.agendamento.states;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import br.com.gestaopsicologica.exceptions.PagamentoPendenteException;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.services.agendamento.helpers.AgendamentoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriadoState implements StatusAtendimentoState {
    private final AgendamentoHelper agendamentoHelper;

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.CRIADO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        StatusPagamento statusPagamento = agendamento.getStatusPagamento();

        switch (novoStatus) {
            case CONCLUIDO -> {
                if (StatusPagamento.PENDENTE.equals(statusPagamento)) {
                    throw new PagamentoPendenteException("Pagamento pendente. Impossível concluir atendimento!");
                }

                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            case CONFIRMADO, CANCELADO, REAGENDADO -> {
                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            default -> throw new StatusAtendimentoInvalidoException(
                    "CRIADO → " + novoStatus + " não permitido"
            );
        }
    }
}

package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import br.com.gestaopsicologica.exceptions.PagamentoPendenteException;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.services.agendamento.helpers.AgendamentoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ConfirmadoStrategy implements StatusAtendimentoStrategy {
    private final AgendamentoHelper agendamentoHelper;

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.CONFIRMADO;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        switch (novoStatus) {
            case EM_ANDAMENTO, CONCLUIDO, CANCELADO -> {
                if (StatusAtendimento.CONCLUIDO.equals(novoStatus)
                        && StatusPagamento.PENDENTE.equals(agendamento.getStatusPagamento())) {
                    throw new PagamentoPendenteException("Pagamento pendente. Impossível concluir atendimento!");
                }

                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            case NAO_COMPARECEU -> {
                if (LocalDateTime.now().isBefore(agendamento.getDataHoraInicio())) {
                    throw new StatusAtendimentoInvalidoException("Não pode marcar NÃO COMPARECEU antes do horário.");
                }

                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            default -> throw new StatusAtendimentoInvalidoException(
                    "CONFIRMADO → " + novoStatus + " não permitido"
            );
        }
    }
}

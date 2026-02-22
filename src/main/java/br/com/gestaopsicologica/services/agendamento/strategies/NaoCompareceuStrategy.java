package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.services.agendamento.helpers.AgendamentoHelper;
import br.com.gestaopsicologica.services.financeiro.FinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NaoCompareceuStrategy implements StatusAtendimentoStrategy {
    private final AgendamentoHelper agendamentoHelper;
    private final FinanceiroService financeiroService;

    @Override
    public StatusAtendimento getStatus() {
        return StatusAtendimento.NAO_COMPARECEU;
    }

    @Override
    public AgendamentoResponse alterarStatus(Agendamento agendamento, StatusAtendimento novoStatus) {
        switch (novoStatus) {

            case CANCELADO -> {

                validarHorario(agendamento);

                financeiroService.gerarCobrancaPorFalta(agendamento);

                return agendamentoHelper.atualizarStatus(agendamento, novoStatus);
            }

            default -> throw new StatusAtendimentoInvalidoException(
                    "NAO_COMPARECEU → " + novoStatus + " não permitido"
            );
        }
    }

    private void validarHorario(Agendamento agendamento) {
        if (LocalDateTime.now().isBefore(agendamento.getDataHoraInicio())) {
            throw new StatusAtendimentoInvalidoException(
                    "Não pode marcar NÃO_COMPARECEU antes do horário"
            );
        }
    }
}

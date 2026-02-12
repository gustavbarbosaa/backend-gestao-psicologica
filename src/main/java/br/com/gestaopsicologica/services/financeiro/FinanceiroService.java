package br.com.gestaopsicologica.services.financeiro;

import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroService {
    public void gerarCobrancaPorFalta(Agendamento agendamento) {
        if (StatusPagamento.CONFIRMADO.equals(agendamento.getStatusPagamento())) {
            return;
        }

        agendamento.setStatusPagamento(StatusPagamento.COBRANCA_GERADA);
    }
}

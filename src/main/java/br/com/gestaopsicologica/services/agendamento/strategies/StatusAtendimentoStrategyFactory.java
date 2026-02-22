package br.com.gestaopsicologica.services.agendamento.strategies;

import br.com.gestaopsicologica.enums.StatusAtendimento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatusAtendimentoStrategyFactory {
    private final Map<StatusAtendimento, StatusAtendimentoStrategy> states;

    public StatusAtendimentoStrategyFactory(List<StatusAtendimentoStrategy> stateList) {
        this.states = stateList.stream()
                .collect(Collectors.toMap(
                        StatusAtendimentoStrategy::getStatus,
                        Function.identity())
                );
    }

    public StatusAtendimentoStrategy getState(StatusAtendimento status) {
        StatusAtendimentoStrategy state = states.get(status);

        if (state == null) {
            throw new IllegalStateException("Estado não suportado: " + status);
        }

        return state;
    }
}

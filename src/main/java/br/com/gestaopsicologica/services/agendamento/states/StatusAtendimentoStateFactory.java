package br.com.gestaopsicologica.services.agendamento.states;

import br.com.gestaopsicologica.enums.StatusAtendimento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatusAtendimentoStateFactory {
    private final Map<StatusAtendimento, StatusAtendimentoState> states;

    public StatusAtendimentoStateFactory(List<StatusAtendimentoState> stateList) {
        this.states = stateList.stream()
                .collect(Collectors.toMap(
                        StatusAtendimentoState::getStatus,
                        Function.identity())
                );
    }

    public StatusAtendimentoState getState(StatusAtendimento status) {
        StatusAtendimentoState state = states.get(status);

        if (state == null) {
            throw new IllegalStateException("Estado não suportado: " + status);
        }

        return state;
    }
}

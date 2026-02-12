package br.com.gestaopsicologica.services.agendamento.helpers;

import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.mappers.AgendamentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgendamentoHelper {

    private final AgendamentoMapper agendamentoMapper;

    public AgendamentoResponse atualizarStatus(Agendamento agendamento, StatusAtendimento statusAtendimento) {
        agendamento.setStatusAtendimento(statusAtendimento);
        return agendamentoMapper.toResponse(agendamento);
    }
}

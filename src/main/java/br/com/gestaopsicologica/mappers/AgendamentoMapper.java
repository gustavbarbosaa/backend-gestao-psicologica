package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.requests.AgendamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {
    AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    AgendamentoResponse toResponse(Agendamento agendamento);

    List<AgendamentoResponse> toResponseList(List<Agendamento> agendamentos);

    AgendamentoRequest toRequest(AgendamentoResponse agendamentoResponse);

    Agendamento toEntity(AgendamentoRequest agendamentoRequest);
}

package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.requests.PacienteRequest;
import br.com.gestaopsicologica.DTO.responses.PacienteMaxResponse;
import br.com.gestaopsicologica.DTO.responses.PacienteMinRespose;
import br.com.gestaopsicologica.domain.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    PacienteMapper INSTANCE = Mappers.getMapper(PacienteMapper.class);

    PacienteMinRespose toMinRespose(Paciente paciente);

    List<PacienteMinRespose> toMinResposeList(List<Paciente> pacientes);

    PacienteMaxResponse toMaxResponse(Paciente paciente);

    List<PacienteMaxResponse> toMaxResponseList(List<Paciente> pacientes);

    Paciente toPaciente(PacienteRequest paciente);
}

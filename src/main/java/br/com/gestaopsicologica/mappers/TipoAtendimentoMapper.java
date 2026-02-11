package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.requests.TipoAtendimentoRequest;
import br.com.gestaopsicologica.DTO.responses.TipoAtendimentoResponse;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoAtendimentoMapper {
    TipoAtendimentoMapper INSTANCE = Mappers.getMapper(TipoAtendimentoMapper.class);

    TipoAtendimentoResponse toResponse(TipoAtendimento tipoAtendimento);

    TipoAtendimentoRequest toRequest(TipoAtendimento tipoAtendimento);

    TipoAtendimento toEntity(TipoAtendimentoRequest tipoAtendimentoRequest);

    TipoAtendimento toEntity(TipoAtendimentoResponse tipoAtendimentoResponse);

    List<TipoAtendimentoResponse> toResponseList(List<TipoAtendimento> tipoAtendimentos);
}

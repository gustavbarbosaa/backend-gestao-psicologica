package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.requests.TipoAtendimentoRequest;
import br.com.gestaopsicologica.DTO.responses.TipoAtendimentoResponse;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoAtendimentoMapper {
    TipoAtendimentoMapper INSTANCE = Mappers.getMapper(TipoAtendimentoMapper.class);

    @Mapping(target = "usuarioId", source = "usuario.id")
    TipoAtendimentoResponse toResponse(TipoAtendimento tipoAtendimento);

    @Mapping(target = "usuarioId", source = "usuario.id")
    TipoAtendimentoRequest toRequest(TipoAtendimento tipoAtendimento);

    @Mapping(target = "usuario", ignore = true)
    TipoAtendimento toEntity(TipoAtendimentoRequest tipoAtendimentoRequest);

    @Mapping(target = "usuario", ignore = true)
    TipoAtendimento toEntity(TipoAtendimentoResponse tipoAtendimentoResponse);

    List<TipoAtendimentoResponse> toResponseList(List<TipoAtendimento> tipoAtendimentos);
}

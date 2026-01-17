package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.responses.UsuarioResponse;
import br.com.gestaopsicologica.domain.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioResponse toResponse(Usuario usuario);

    Usuario toEntity(UsuarioResponse usuarioResponse);
}

package br.com.gestaopsicologica.mappers;

import br.com.gestaopsicologica.DTO.requests.EvolucaoPsicoterapicaRequest;
import br.com.gestaopsicologica.DTO.responses.EvolucaoPsicoterapicaResponse;
import br.com.gestaopsicologica.domain.EvolucaoPsicoterapica;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvolucaoPsicoterapicaMapper {
    EvolucaoPsicoterapica toEntity(EvolucaoPsicoterapicaRequest evolucao);

    EvolucaoPsicoterapicaResponse toDTO(EvolucaoPsicoterapica evolucao);

    List<EvolucaoPsicoterapicaResponse> listToDTO(List<EvolucaoPsicoterapica> evolucoes);
}

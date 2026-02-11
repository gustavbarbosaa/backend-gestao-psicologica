package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.TipoAtendimentoRequest;
import br.com.gestaopsicologica.DTO.responses.TipoAtendimentoResponse;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import br.com.gestaopsicologica.mappers.TipoAtendimentoMapper;
import br.com.gestaopsicologica.repository.TipoAtendimentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoAtendimentoService {
    private final TipoAtendimentoRepository tipoAtendimentoRepository;
    private final TipoAtendimentoMapper tipoAtendimentoMapper;

    private static final String TIPO_ATENDIMENTO_NAO_ENCONTRADO =
            "Não foi encontrado nenhum tipo de atendimento correspondente!";

    public List<TipoAtendimentoResponse> verTiposAtendimento() {
        List<TipoAtendimento> tipoAtendimentos = tipoAtendimentoRepository.findAll();

        return tipoAtendimentoMapper.toResponseList(tipoAtendimentos);
    }

    public TipoAtendimentoResponse verTipoAtendimentoPorId(UUID idTipoAtendimento) {
        TipoAtendimento tipoAtendimento = tipoAtendimentoRepository.findById(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        return tipoAtendimentoMapper.toResponse(tipoAtendimento);
    }

    @Transactional
    public TipoAtendimentoRequest criarTipoAtendimento(TipoAtendimentoRequest tipoAtendimentoRequest) {
        TipoAtendimento tipoAtendimento = tipoAtendimentoMapper.toEntity(tipoAtendimentoRequest);
        tipoAtendimento.setAtivo(true);
        tipoAtendimentoRepository.save(tipoAtendimento);

        return new TipoAtendimentoRequest(tipoAtendimentoRequest.tipoAtendimento(), tipoAtendimentoRequest.valorPadraoTipoAtendimento());
    }

    @Transactional
    public TipoAtendimentoResponse editarTipoAtendimento(UUID idTipoAtendimento, TipoAtendimentoRequest tipoAtendimentoRequest) {
        TipoAtendimento tipoAtendimentoExistente = tipoAtendimentoRepository.findById(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        if (tipoAtendimentoExistente.getNome() != tipoAtendimentoRequest.tipoAtendimento()) {
            tipoAtendimentoExistente.setNome(tipoAtendimentoRequest.tipoAtendimento());
        }

        if (tipoAtendimentoExistente.getValorPadraoTipoAtendimento() != tipoAtendimentoRequest.valorPadraoTipoAtendimento()) {
            tipoAtendimentoExistente.setValorPadraoTipoAtendimento(tipoAtendimentoRequest.valorPadraoTipoAtendimento());
        }

        tipoAtendimentoRepository.save(tipoAtendimentoExistente);
        return tipoAtendimentoMapper.toResponse(tipoAtendimentoExistente);
    }

    @Transactional
    public void removerTipoAtendimento(UUID idTipoAtendimento) {
        TipoAtendimento tipoAtendimento = tipoAtendimentoRepository.findById(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        tipoAtendimento.setAtivo(false);
        tipoAtendimentoRepository.save(tipoAtendimento);
    }
}

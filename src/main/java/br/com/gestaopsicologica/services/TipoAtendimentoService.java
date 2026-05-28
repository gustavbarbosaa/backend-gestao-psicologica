package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.TipoAtendimentoRequest;
import br.com.gestaopsicologica.DTO.responses.TipoAtendimentoResponse;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.mappers.TipoAtendimentoMapper;
import br.com.gestaopsicologica.repository.TipoAtendimentoRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoAtendimentoService {
    private final TipoAtendimentoRepository tipoAtendimentoRepository;
    private final TipoAtendimentoMapper tipoAtendimentoMapper;
    private final UsuarioRepository usuarioRepository;

    private static final String TIPO_ATENDIMENTO_NAO_ENCONTRADO =
            "Não foi encontrado nenhum tipo de atendimento correspondente!";

    public List<TipoAtendimentoResponse> verTiposAtendimento() {
        List<TipoAtendimento> tipoAtendimentos = isAdmin()
                ? tipoAtendimentoRepository.findAll()
                : tipoAtendimentoRepository.findAllByUsuarioId(getAuthenticatedUserId());

        return tipoAtendimentoMapper.toResponseList(tipoAtendimentos);
    }

    public TipoAtendimentoResponse verTipoAtendimentoPorId(UUID idTipoAtendimento) {
        TipoAtendimento tipoAtendimento = findTipoAtendimentoByScope(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        return tipoAtendimentoMapper.toResponse(tipoAtendimento);
    }

    @Transactional
    public TipoAtendimentoRequest criarTipoAtendimento(TipoAtendimentoRequest tipoAtendimentoRequest) {
        UUID usuarioId = resolveTargetUsuarioId(tipoAtendimentoRequest.usuarioId());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        TipoAtendimento tipoAtendimento = tipoAtendimentoMapper.toEntity(tipoAtendimentoRequest);
        tipoAtendimento.setAtivo(true);
        tipoAtendimento.setUsuario(usuario);
        tipoAtendimentoRepository.save(tipoAtendimento);

        return new TipoAtendimentoRequest(
                tipoAtendimentoRequest.nome(),
                tipoAtendimentoRequest.valorPadraoTipoAtendimento(),
                usuario.getId()
        );
    }

    @Transactional
    public TipoAtendimentoResponse editarTipoAtendimento(UUID idTipoAtendimento, TipoAtendimentoRequest tipoAtendimentoRequest) {
        TipoAtendimento tipoAtendimentoExistente = findTipoAtendimentoByScope(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        if (tipoAtendimentoExistente.getNome() != tipoAtendimentoRequest.nome()) {
            tipoAtendimentoExistente.setNome(tipoAtendimentoRequest.nome());
        }

        if (!Objects.equals(tipoAtendimentoExistente.getValorPadraoTipoAtendimento(), tipoAtendimentoRequest.valorPadraoTipoAtendimento())) {
            tipoAtendimentoExistente.setValorPadraoTipoAtendimento(tipoAtendimentoRequest.valorPadraoTipoAtendimento());
        }

        UUID usuarioAtualId = tipoAtendimentoExistente.getUsuario() != null
                ? tipoAtendimentoExistente.getUsuario().getId()
                : null;
        UUID usuarioIdDestino = tipoAtendimentoRequest.usuarioId() != null
                ? resolveTargetUsuarioId(tipoAtendimentoRequest.usuarioId())
                : usuarioAtualId;
        if (!Objects.equals(usuarioIdDestino, usuarioAtualId)) {
            Usuario usuario = usuarioRepository.findById(usuarioIdDestino)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
            tipoAtendimentoExistente.setUsuario(usuario);
        }

        tipoAtendimentoRepository.save(tipoAtendimentoExistente);
        return tipoAtendimentoMapper.toResponse(tipoAtendimentoExistente);
    }

    @Transactional
    public void removerTipoAtendimento(UUID idTipoAtendimento) {
        TipoAtendimento tipoAtendimento = findTipoAtendimentoByScope(idTipoAtendimento)
                .orElseThrow(() -> new EntityNotFoundException(TIPO_ATENDIMENTO_NAO_ENCONTRADO));

        tipoAtendimento.setAtivo(false);
        tipoAtendimentoRepository.save(tipoAtendimento);
    }

    private Optional<TipoAtendimento> findTipoAtendimentoByScope(UUID idTipoAtendimento) {
        if (isAdmin()) {
            return tipoAtendimentoRepository.findById(idTipoAtendimento);
        }

        return tipoAtendimentoRepository.findByIdAndUsuarioId(idTipoAtendimento, getAuthenticatedUserId());
    }

    private UUID resolveTargetUsuarioId(UUID requestedUsuarioId) {
        if (isAdmin() && requestedUsuarioId != null) {
            return requestedUsuarioId;
        }

        return getAuthenticatedUserId();
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "PAPEL_ADMIN".equals(authority.getAuthority()));
    }
}

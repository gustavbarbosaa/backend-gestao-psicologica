package br.com.gestaopsicologica.services.agendamento;

import br.com.gestaopsicologica.DTO.requests.AgendamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import br.com.gestaopsicologica.exceptions.StatusAtendimentoInvalidoException;
import br.com.gestaopsicologica.mappers.AgendamentoMapper;
import br.com.gestaopsicologica.repository.AgendamentoRepository;
import br.com.gestaopsicologica.repository.PacienteRepository;
import br.com.gestaopsicologica.repository.TipoAtendimentoRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import br.com.gestaopsicologica.services.agendamento.strategies.StatusAtendimentoStrategy;
import br.com.gestaopsicologica.services.agendamento.strategies.StatusAtendimentoStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoAtendimentoRepository tipoAtendimentoRepository;
    private final StatusAtendimentoStrategyFactory statusAtendimentoStateFactory;

    private static final String AGENDAMENTO_NAO_ENCONTRADO = "Agendamento não encontrado.";

    @Transactional
    public AgendamentoResponse criarAgendamento(AgendamentoRequest agendamentoRequest) {
        UUID usuarioId = resolveTargetUsuarioId(agendamentoRequest.usuarioId());

        validaDisponibilidadeDeHorario(
                usuarioId,
                agendamentoRequest.dataHoraInicio(),
                agendamentoRequest.duracaoEmMinutos(),
                null
        );

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Paciente paciente = findPacienteByScope(agendamentoRequest.pacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        TipoAtendimento tipoAtendimento = findTipoAtendimentoByScope(agendamentoRequest.tipoAtendimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de atendimento não encontrado"));
        validarTipoAtendimentoDoProfissional(tipoAtendimento, usuarioId);

        Agendamento agendamento = agendamentoMapper.toEntity(agendamentoRequest);
        agendamento.setUsuario(usuario);
        agendamento.setPaciente(paciente);
        agendamento.setStatusAtendimento(StatusAtendimento.CRIADO);
        agendamento.setTipoAtendimento(tipoAtendimento);
        agendamento.setValorAtendimento(tipoAtendimento.getValorPadraoTipoAtendimento());

        if (agendamento.getStatusPagamento() == null) {
            agendamento.setStatusPagamento(StatusPagamento.PENDENTE);
        }

        agendamento = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponse(agendamento);
    }

    public List<AgendamentoResponse> listarTodosAgendamentos() {
        List<Agendamento> agendamentos = isAdmin()
                ? agendamentoRepository.findAll()
                : agendamentoRepository.findAgendamentosByUsuarioId(getAuthenticatedUserId());
        return agendamentoMapper.toResponseList(agendamentos);
    }

    public List<AgendamentoResponse> listarAgendamentosPorPaciente(UUID pacienteId) {
        List<Agendamento> agendamentos = isAdmin()
                ? agendamentoRepository.findAgendamentosByPacienteId(pacienteId)
                : agendamentoRepository.findAgendamentosByPacienteIdAndUsuarioId(pacienteId, getAuthenticatedUserId());
        return agendamentoMapper.toResponseList(agendamentos);
    }

    public List<AgendamentoResponse> listarAgendamentosPorUsuario() {
        return agendamentoMapper.toResponseList(agendamentoRepository.findAgendamentosByUsuarioId(getAuthenticatedUserId()));
    }

    @Transactional
    public void apagarAgendamento(UUID agendamentoId) {
        if (findAgendamentoByScope(agendamentoId).isEmpty()) {
            throw new EntityNotFoundException("Agendamento com ID " + agendamentoId + " não encontrado para exclusão.");
        }

        agendamentoRepository.deleteById(agendamentoId);
    }

    @Transactional
    public AgendamentoResponse editarAgendamento(UUID agendamentoId, AgendamentoRequest agendamentoRequest) {

        Agendamento agendamentoExistente = findAgendamentoByScope(agendamentoId)
                .orElseThrow(() -> new EntityNotFoundException(AGENDAMENTO_NAO_ENCONTRADO));

        boolean mudouHorario = agendamentoRequest.dataHoraInicio() != null && !agendamentoRequest.dataHoraInicio().equals(agendamentoExistente.getDataHoraInicio());
        boolean mudouDuracao = agendamentoRequest.duracaoEmMinutos() != null && !agendamentoRequest.duracaoEmMinutos().equals(agendamentoExistente.getDuracaoEmMinutos());
        UUID usuarioIdDestino = resolveTargetUsuarioId(agendamentoRequest.usuarioId());
        boolean mudouProfissional = !usuarioIdDestino.equals(agendamentoExistente.getUsuario().getId());
        boolean mudouTipoAgendamento = agendamentoRequest.tipoAtendimentoId() != null && !agendamentoRequest.tipoAtendimentoId().equals(agendamentoExistente.getTipoAtendimento().getId());

        if (mudouHorario || mudouDuracao || mudouProfissional) {
            LocalDateTime novoInicio = mudouHorario ? agendamentoRequest.dataHoraInicio() : agendamentoExistente.getDataHoraInicio();
            Integer novaDuracao = mudouDuracao ? agendamentoRequest.duracaoEmMinutos() : agendamentoExistente.getDuracaoEmMinutos();
            UUID idProfissional = mudouProfissional ? usuarioIdDestino : agendamentoExistente.getUsuario().getId();

            validaDisponibilidadeDeHorario(idProfissional, novoInicio, novaDuracao, agendamentoId);
        }

        if (mudouHorario) agendamentoExistente.setDataHoraInicio(agendamentoRequest.dataHoraInicio());
        if (mudouDuracao) agendamentoExistente.setDuracaoEmMinutos(agendamentoRequest.duracaoEmMinutos());
        if (mudouProfissional) {
            Usuario usuario = usuarioRepository.findById(usuarioIdDestino)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            agendamentoExistente.setUsuario(usuario);
            if (!mudouTipoAgendamento) {
                validarTipoAtendimentoDoProfissional(agendamentoExistente.getTipoAtendimento(), usuarioIdDestino);
            }
        }
        if (mudouTipoAgendamento) {
            UUID usuarioIdTipoAtendimento = mudouProfissional
                    ? usuarioIdDestino
                    : agendamentoExistente.getUsuario().getId();

            TipoAtendimento tipoAtendimento = findTipoAtendimentoByScope(agendamentoRequest.tipoAtendimentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de atendimento não encontrado"));
            validarTipoAtendimentoDoProfissional(tipoAtendimento, usuarioIdTipoAtendimento);
            agendamentoExistente.setTipoAtendimento(tipoAtendimento);
            agendamentoExistente.setValorAtendimento(tipoAtendimento.getValorPadraoTipoAtendimento());
        }

        return agendamentoMapper.toResponse(agendamentoRepository.save(agendamentoExistente));
    }

    @Transactional
    public AgendamentoResponse alterarStatusAtendimento(UUID agendamentoId, StatusAtendimento novoStatus) {
        Agendamento agendamentoExistente = findAgendamentoByScope(agendamentoId)
                .orElseThrow(() -> new EntityNotFoundException(AGENDAMENTO_NAO_ENCONTRADO));

        if (agendamentoExistente.getStatusAtendimento().equals(novoStatus)) {
            throw new StatusAtendimentoInvalidoException("Atendimento já está no status " + novoStatus);
        }

        StatusAtendimentoStrategy state = statusAtendimentoStateFactory.getState(agendamentoExistente.getStatusAtendimento());

        return state.alterarStatus(agendamentoExistente, novoStatus);
    }

    @Transactional
    public AgendamentoResponse alterarStatusPagamentoAtendimento(UUID agendamentoId, StatusPagamento statusPagamento) {
        Agendamento agendamentoExistente = findAgendamentoByScope(agendamentoId)
                .orElseThrow(() -> new EntityNotFoundException(AGENDAMENTO_NAO_ENCONTRADO));

        if (agendamentoExistente.getStatusPagamento() == statusPagamento) {
            throw new IllegalArgumentException("Pagamento já está no status " + statusPagamento);
        }

        agendamentoExistente.setStatusPagamento(statusPagamento);

        return agendamentoMapper.toResponse(agendamentoRepository.save(agendamentoExistente));
    }

    private void validaDisponibilidadeDeHorario(UUID usuarioId, LocalDateTime dataHoraInicio, Integer duracaoEmMinutos, UUID agendamentoIdParaIgnorar) {
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(duracaoEmMinutos);

        LocalDateTime inicioDia = dataHoraInicio.toLocalDate().atStartOfDay();
        LocalDateTime fimDia = dataHoraInicio.toLocalDate().atTime(LocalTime.MAX);

        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findAgendamentosByUsuarioIdAndDataHoraInicioBetween(usuarioId, inicioDia, fimDia);

        for (Agendamento existente : agendamentosDoDia) {
            if (existente.getId().equals(agendamentoIdParaIgnorar)) {
                continue;
            }

            LocalDateTime existenteFim = existente.getDataHoraFim();


            if (dataHoraInicio.isBefore(existenteFim) && dataHoraFim.isAfter(existente.getDataHoraInicio())) {
                throw new IllegalArgumentException("Conflito de horário! Já existe agendamento das "
                        + existente.getDataHoraInicio().toLocalTime() + " às " + existenteFim.toLocalTime());
            }
        }
    }

    private Optional<Agendamento> findAgendamentoByScope(UUID agendamentoId) {
        if (isAdmin()) {
            return agendamentoRepository.findById(agendamentoId);
        }

        return agendamentoRepository.findByIdAndUsuarioId(agendamentoId, getAuthenticatedUserId());
    }

    private Optional<Paciente> findPacienteByScope(UUID pacienteId) {
        if (isAdmin()) {
            return pacienteRepository.findById(pacienteId);
        }

        return pacienteRepository.findByIdAndUsuarioId(pacienteId, getAuthenticatedUserId());
    }

    private Optional<TipoAtendimento> findTipoAtendimentoByScope(UUID tipoAtendimentoId) {
        if (isAdmin()) {
            return tipoAtendimentoRepository.findById(tipoAtendimentoId);
        }

        return tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, getAuthenticatedUserId());
    }

    private void validarTipoAtendimentoDoProfissional(TipoAtendimento tipoAtendimento, UUID usuarioId) {
        UUID usuarioDonoTipoAtendimento = tipoAtendimento.getUsuario() != null
                ? tipoAtendimento.getUsuario().getId()
                : null;

        if (!Objects.equals(usuarioDonoTipoAtendimento, usuarioId)) {
            throw new IllegalArgumentException("O tipo de atendimento não pertence ao profissional informado.");
        }
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

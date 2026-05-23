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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
        validaDisponibilidadeDeHorario(
                agendamentoRequest.usuarioId(),
                agendamentoRequest.dataHoraInicio(),
                agendamentoRequest.duracaoEmMinutos(),
                null
        );

        Usuario usuario = usuarioRepository.findById(agendamentoRequest.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Paciente paciente = pacienteRepository.findById(agendamentoRequest.pacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        TipoAtendimento tipoAtendimento = tipoAtendimentoRepository.findById(agendamentoRequest.tipoAtendimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de atendimento não encontrado"));

        Agendamento agendamento = agendamentoMapper.toEntity(agendamentoRequest);
        agendamento.setUsuario(usuario);
        agendamento.setPaciente(paciente);
        agendamento.setStatusAtendimento(StatusAtendimento.CRIADO);
        agendamento.setTipoAtendimento(tipoAtendimento);

        if (agendamento.getStatusPagamento() == null) {
            agendamento.setStatusPagamento(StatusPagamento.PENDENTE);
        }

        agendamento = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponse(agendamento);
    }

    public List<AgendamentoResponse> listarTodosAgendamentos() {
        return agendamentoMapper.toResponseList(agendamentoRepository.findAll());
    }

    public List<AgendamentoResponse> listarAgendamentosPorPaciente(UUID pacienteId) {
        return agendamentoMapper.toResponseList(agendamentoRepository.findAgendamentosByPacienteId(pacienteId));
    }

    public List<AgendamentoResponse> listarAgendamentosPorUsuario() {
        UUID usuarioId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        return agendamentoMapper.toResponseList(agendamentoRepository.findAgendamentosByUsuarioId(usuarioId));
    }

    @Transactional
    public void apagarAgendamento(UUID agendamentoId) {
        if (!agendamentoRepository.existsById(agendamentoId)) {
            throw new EntityNotFoundException("Agendamento com ID " + agendamentoId + " não encontrado para exclusão.");
        }

        agendamentoRepository.deleteById(agendamentoId);
    }

    @Transactional
    public AgendamentoResponse editarAgendamento(UUID agendamentoId, AgendamentoRequest agendamentoRequest) {

        Agendamento agendamentoExistente = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new EntityNotFoundException(AGENDAMENTO_NAO_ENCONTRADO));

        boolean mudouHorario = agendamentoRequest.dataHoraInicio() != null && !agendamentoRequest.dataHoraInicio().equals(agendamentoExistente.getDataHoraInicio());
        boolean mudouDuracao = agendamentoRequest.duracaoEmMinutos() != null && !agendamentoRequest.duracaoEmMinutos().equals(agendamentoExistente.getDuracaoEmMinutos());
        boolean mudouProfissional = agendamentoRequest.usuarioId() != null && !agendamentoRequest.usuarioId().equals(agendamentoExistente.getUsuario().getId());
        boolean mudouTipoAgendamento = agendamentoRequest.tipoAtendimentoId() != null && !agendamentoRequest.tipoAtendimentoId().equals(agendamentoExistente.getTipoAtendimento().getId());

        if (mudouHorario || mudouDuracao || mudouProfissional) {
            LocalDateTime novoInicio = mudouHorario ? agendamentoRequest.dataHoraInicio() : agendamentoExistente.getDataHoraInicio();
            Integer novaDuracao = mudouDuracao ? agendamentoRequest.duracaoEmMinutos() : agendamentoExistente.getDuracaoEmMinutos();
            UUID idProfissional = mudouProfissional ? agendamentoRequest.usuarioId() : agendamentoExistente.getUsuario().getId();

            validaDisponibilidadeDeHorario(idProfissional, novoInicio, novaDuracao, agendamentoId);
        }

        if (mudouHorario) agendamentoExistente.setDataHoraInicio(agendamentoRequest.dataHoraInicio());
        if (mudouDuracao) agendamentoExistente.setDuracaoEmMinutos(agendamentoRequest.duracaoEmMinutos());
        if (mudouTipoAgendamento) {
            TipoAtendimento tipoAtendimento = tipoAtendimentoRepository.findById(agendamentoRequest.tipoAtendimentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de atendimento não encontrado"));
            agendamentoExistente.setTipoAtendimento(tipoAtendimento);
        }

        return agendamentoMapper.toResponse(agendamentoRepository.save(agendamentoExistente));
    }

    @Transactional
    public AgendamentoResponse alterarStatusAtendimento(UUID agendamentoId, StatusAtendimento novoStatus) {
        Agendamento agendamentoExistente = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new EntityNotFoundException(AGENDAMENTO_NAO_ENCONTRADO));

        if (agendamentoExistente.getStatusAtendimento().equals(novoStatus)) {
            throw new StatusAtendimentoInvalidoException("Atendimento já está no status " + novoStatus);
        }

        StatusAtendimentoStrategy state = statusAtendimentoStateFactory.getState(agendamentoExistente.getStatusAtendimento());

        return state.alterarStatus(agendamentoExistente, novoStatus);
    }

    @Transactional
    public AgendamentoResponse alterarStatusPagamentoAtendimento(UUID agendamentoId, StatusPagamento statusPagamento) {
        Agendamento agendamentoExistente = agendamentoRepository.findById(agendamentoId)
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
            if (agendamentoIdParaIgnorar != null && existente.getId().equals(agendamentoIdParaIgnorar)) {
                continue;
            }

            LocalDateTime existenteFim = existente.getDataHoraFim();


            if (dataHoraInicio.isBefore(existenteFim) && dataHoraFim.isAfter(existente.getDataHoraInicio())) {
                throw new IllegalArgumentException("Conflito de horário! Já existe agendamento das "
                        + existente.getDataHoraInicio().toLocalTime() + " às " + existenteFim.toLocalTime());
            }
        }
    }
}

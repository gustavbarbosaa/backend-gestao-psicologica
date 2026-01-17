package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.AgendamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.enums.StatusPagamento;
import br.com.gestaopsicologica.mappers.AgendamentoMapper;
import br.com.gestaopsicologica.repository.AgendamentoRepository;
import br.com.gestaopsicologica.repository.PacienteRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        Agendamento agendamento = agendamentoMapper.toEntity(agendamentoRequest);
        agendamento.setUsuario(usuario);
        agendamento.setPaciente(paciente);

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

    public List<AgendamentoResponse> listarAgendamentosPorUsuario(UUID usuarioId) {
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
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado."));

        boolean mudouHorario = agendamentoRequest.dataHoraInicio() != null && !agendamentoRequest.dataHoraInicio().equals(agendamentoExistente.getDataHoraInicio());
        boolean mudouDuracao = agendamentoRequest.duracaoEmMinutos() != null && !agendamentoRequest.duracaoEmMinutos().equals(agendamentoExistente.getDuracaoEmMinutos());
        boolean mudouProfissional = agendamentoRequest.usuarioId() != null && !agendamentoRequest.usuarioId().equals(agendamentoExistente.getUsuario().getId());

        if (mudouHorario || mudouDuracao || mudouProfissional) {
            LocalDateTime novoInicio = mudouHorario ? agendamentoRequest.dataHoraInicio() : agendamentoExistente.getDataHoraInicio();
            Integer novaDuracao = mudouDuracao ? agendamentoRequest.duracaoEmMinutos() : agendamentoExistente.getDuracaoEmMinutos();
            UUID idProfissional = mudouProfissional ? agendamentoRequest.usuarioId() : agendamentoExistente.getUsuario().getId();

            validaDisponibilidadeDeHorario(idProfissional, novoInicio, novaDuracao, agendamentoId);
        }

        if (mudouHorario) agendamentoExistente.setDataHoraInicio(agendamentoRequest.dataHoraInicio());
        if (mudouDuracao) agendamentoExistente.setDuracaoEmMinutos(agendamentoRequest.duracaoEmMinutos());

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

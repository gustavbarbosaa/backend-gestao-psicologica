package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findAgendamentosByUsuarioId(UUID id);

    List<Agendamento> findAgendamentosByPacienteId(UUID pacienteId);

    List<Agendamento> findAgendamentosByUsuarioIdAndDataHoraInicioBetween(UUID id, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim);
}

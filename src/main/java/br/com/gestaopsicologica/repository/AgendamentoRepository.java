package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByAtivoTrue();

    List<Agendamento> findAgendamentosByUsuarioId(UUID id);

    List<Agendamento> findAgendamentosByUsuarioIdAndAtivoTrue(UUID id);

    List<Agendamento> findAgendamentosByPacienteId(UUID pacienteId);

    List<Agendamento> findAgendamentosByPacienteIdAndUsuarioId(UUID pacienteId, UUID usuarioId);

    Optional<Agendamento> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<Agendamento> findAgendamentosByUsuarioIdAndDataHoraInicioBetween(UUID id, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim);
}

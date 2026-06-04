package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.EvolucaoPsicoterapica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvolucaoPsicoterapicaRepository extends JpaRepository<EvolucaoPsicoterapica, UUID> {
    List<EvolucaoPsicoterapica> findByAtivoTrueAndAgendamentoUsuarioId(UUID usuarioId);

    List<EvolucaoPsicoterapica> findByAtivoTrueAndAgendamentoUsuarioIdAndAgendamentoPacienteId(UUID usuarioId, UUID pacienteId);

    Optional<EvolucaoPsicoterapica> findByIdAndAtivoTrueAndAgendamentoUsuarioId(UUID evolucaoId, UUID usuarioId);
}

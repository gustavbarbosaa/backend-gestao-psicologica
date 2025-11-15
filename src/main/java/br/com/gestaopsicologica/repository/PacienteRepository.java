package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
}

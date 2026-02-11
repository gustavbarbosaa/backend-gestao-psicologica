package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.TipoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TipoAtendimentoRepository extends JpaRepository<TipoAtendimento, UUID> {
}

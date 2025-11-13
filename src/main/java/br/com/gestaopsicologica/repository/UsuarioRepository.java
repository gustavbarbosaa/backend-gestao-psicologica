package br.com.gestaopsicologica.repository;

import br.com.gestaopsicologica.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Override
    Optional<Usuario> findById(UUID uuid);

    Optional<Usuario> findUsuarioByEmail(String email);
}

package br.com.gestaopsicologica.config;

import br.com.gestaopsicologica.domain.Papel;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.repository.PapelRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Order(1)
@RequiredArgsConstructor
public class AdminBootstrapRunner implements ApplicationRunner {
    private static final String ADMIN_ROLE_NAME = "ADMIN";

    private final AdminBootstrapProperties adminBootstrapProperties;
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!adminBootstrapProperties.enabled()) {
            return;
        }

        validateRequiredProperties();

        Papel adminRole = papelRepository.findByNome(ADMIN_ROLE_NAME)
                .orElseGet(() -> papelRepository.save(Papel.builder().nome(ADMIN_ROLE_NAME).build()));

        boolean adminAlreadyExists = usuarioRepository.existsByPapeisNome(ADMIN_ROLE_NAME);
        if (adminAlreadyExists) {
            return;
        }

        Usuario existingUser = usuarioRepository.findUsuarioByEmail(adminBootstrapProperties.email().trim()).orElse(null);
        if (existingUser != null) {
            existingUser.setNome(adminBootstrapProperties.name().trim());
            existingUser.setSenha(passwordEncoder.encode(adminBootstrapProperties.password()));
            existingUser.getPapeis().add(adminRole);
            usuarioRepository.save(existingUser);
            return;
        }

        Usuario admin = Usuario.builder()
                .nome(adminBootstrapProperties.name().trim())
                .email(adminBootstrapProperties.email().trim())
                .senha(passwordEncoder.encode(adminBootstrapProperties.password()))
                .papeis(Set.of(adminRole))
                .build();

        usuarioRepository.save(admin);
    }

    private void validateRequiredProperties() {
        if (isBlank(adminBootstrapProperties.name())
                || isBlank(adminBootstrapProperties.email())
                || isBlank(adminBootstrapProperties.password())) {
            throw new IllegalStateException("Defina ADMIN_NAME, ADMIN_EMAIL e ADMIN_PASSWORD para criar o admin inicial.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

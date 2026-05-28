package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.domain.Papel;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.enums.Papeis;
import br.com.gestaopsicologica.repository.PapelRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CadastroResponse criarUsuario(CadastroRequest cadastroUsuarioRequest) {
        if (usuarioRepository.findUsuarioByEmail(cadastroUsuarioRequest.email()).isPresent()) {
            throw new IllegalArgumentException("O e-mail informado já está cadastrado.");
        }

        Set<Papel> papeis = new HashSet<>();
        Papel papel = papelRepository.findByNome(Papeis.PROFISSIONAL.getDescricao())
                .orElseThrow(() -> new EntityNotFoundException("Papel não encontrado!"));
        papeis.add(papel);

        Usuario novoUsuario = Usuario.builder()
                .nome(cadastroUsuarioRequest.nome())
                .email(cadastroUsuarioRequest.email())
                .senha(passwordEncoder.encode(cadastroUsuarioRequest.senha()))
                .papeis(papeis)
                .build();

        usuarioRepository.save(novoUsuario);

        return new CadastroResponse(novoUsuario.getId(), novoUsuario.getEmail(), novoUsuario.getNome());
    }
}

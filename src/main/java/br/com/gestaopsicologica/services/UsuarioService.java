package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CadastroResponse criarUsuario(CadastroRequest cadastroUsuarioRequest) throws Exception {
        if (usuarioRepository.findUsuarioByEmail(cadastroUsuarioRequest.email()).isPresent()) {
            throw new IllegalArgumentException("O e-mail informado já está cadastrado.");
        }

        Usuario novoUsuario = Usuario.builder()
                .nome(cadastroUsuarioRequest.nome())
                .email(cadastroUsuarioRequest.email())
                .senha(passwordEncoder.encode(cadastroUsuarioRequest.senha()))
                .build();

        usuarioRepository.save(novoUsuario);

        return new CadastroResponse(novoUsuario.getId(), novoUsuario.getEmail(), novoUsuario.getNome());
    }
}

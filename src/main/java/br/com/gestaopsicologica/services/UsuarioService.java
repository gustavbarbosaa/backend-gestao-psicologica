package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.requests.LoginRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.config.TokenConfig;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public LoginResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenConfig.geraToken(usuario);

        return new LoginResponse(token);
    }

    public CadastroResponse criarUsuario(CadastroRequest cadastroUsuarioRequest) throws Exception {
        try {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(cadastroUsuarioRequest.nome());
            novoUsuario.setEmail(cadastroUsuarioRequest.email());
            novoUsuario.setSenha(passwordEncoder.encode(cadastroUsuarioRequest.senha()));

            usuarioRepository.save(novoUsuario);

            return new CadastroResponse(novoUsuario.getId(), novoUsuario.getEmail(), novoUsuario.getNome());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

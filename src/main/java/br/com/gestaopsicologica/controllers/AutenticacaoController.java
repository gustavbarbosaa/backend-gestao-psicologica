package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.requests.LoginRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.DTO.responses.UsuarioResponse;
import br.com.gestaopsicologica.config.TokenConfig;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.mappers.UsuarioMapper;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import br.com.gestaopsicologica.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/autenticacao")
@RequiredArgsConstructor
public class AutenticacaoController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
                );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenConfig.geraToken(usuario);
        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);

        return ResponseEntity.ok(new LoginResponse(token, usuarioResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@Valid @RequestBody CadastroRequest cadastroRequest) throws Exception {
        CadastroResponse response = usuarioService.criarUsuario(cadastroRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID usuarioID = (UUID) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findById(usuarioID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok().body(new UsuarioResponse(usuario));
    }
}

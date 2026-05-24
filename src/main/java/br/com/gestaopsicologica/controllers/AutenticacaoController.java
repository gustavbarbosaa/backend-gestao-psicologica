package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.requests.LoginRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.DTO.responses.UsuarioResponse;
import br.com.gestaopsicologica.config.SecurityProperties;
import br.com.gestaopsicologica.config.TokenConfig;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.mappers.UsuarioMapper;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import br.com.gestaopsicologica.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/autenticacao")
@RequiredArgsConstructor
public class AutenticacaoController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final SecurityProperties securityProperties;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
                );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenConfig.geraToken(usuario);
        SecurityProperties.Cookie cookieProperties = securityProperties.cookie();

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .path("/")
                .maxAge(Duration.ofSeconds(cookieProperties.maxAge()))
                .domain(cookieProperties.domain())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);

        return ResponseEntity.ok(new LoginResponse(usuarioResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        SecurityProperties.Cookie cookieProperties = securityProperties.cookie();

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .path("/")
                .maxAge(0)
                .domain(cookieProperties.domain())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@Valid @RequestBody CadastroRequest cadastroRequest) throws Exception {
        CadastroResponse response = usuarioService.criarUsuario(cadastroRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(CsrfToken csrfToken) {
        csrfToken.getToken();
        return ResponseEntity.noContent().build();
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

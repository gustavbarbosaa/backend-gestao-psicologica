package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.requests.LoginRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.config.TokenConfig;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.services.UsuarioService;
import jakarta.servlet.ServletException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/autenticacao")
@RequiredArgsConstructor
public class AutenticacaoController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
                );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenConfig.geraToken(usuario);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@Valid @RequestBody CadastroRequest cadastroRequest) throws Exception {
        CadastroResponse response = usuarioService.criarUsuario(cadastroRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

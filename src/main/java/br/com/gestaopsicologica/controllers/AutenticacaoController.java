package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.CadastroRequest;
import br.com.gestaopsicologica.DTO.requests.LoginRequest;
import br.com.gestaopsicologica.DTO.responses.CadastroResponse;
import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/autenticacao")
@RequiredArgsConstructor
public class AutenticacaoController {
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = usuarioService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@Valid @RequestBody CadastroRequest cadastroRequest) throws Exception {
        CadastroResponse response = usuarioService.criarUsuario(cadastroRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

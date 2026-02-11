package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.TipoAtendimentoRequest;
import br.com.gestaopsicologica.DTO.responses.TipoAtendimentoResponse;
import br.com.gestaopsicologica.services.TipoAtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tipo-atendimento")
@RequiredArgsConstructor
public class TipoAtendimentoController {
    private final TipoAtendimentoService tipoAtendimentoService;

    @GetMapping
    public ResponseEntity<List<TipoAtendimentoResponse>> listarTodosTipoDeAtendimento() {
        return ResponseEntity.ok().body(tipoAtendimentoService.verTiposAtendimento());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAtendimentoResponse> listarTipoDeAtendimento(@PathVariable UUID id) {
        return ResponseEntity.ok().body(tipoAtendimentoService.verTipoAtendimentoPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoAtendimentoRequest> criarTipoAtendimento(@Valid @RequestBody TipoAtendimentoRequest tipoAtendimentoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoAtendimentoService.criarTipoAtendimento(tipoAtendimentoRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TipoAtendimentoResponse> editarTipoAtendimento(@PathVariable UUID id, @Valid @RequestBody TipoAtendimentoRequest tipoAtendimentoRequest) {
        return ResponseEntity.ok().body(tipoAtendimentoService.editarTipoAtendimento(id, tipoAtendimentoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarTipoAtendimento(@PathVariable UUID id) {
        tipoAtendimentoService.removerTipoAtendimento(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

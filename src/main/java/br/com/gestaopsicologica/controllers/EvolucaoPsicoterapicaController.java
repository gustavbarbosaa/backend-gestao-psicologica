package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.EvolucaoPsicoterapicaRequest;
import br.com.gestaopsicologica.DTO.responses.EvolucaoPsicoterapicaResponse;
import br.com.gestaopsicologica.services.EvolucaoPsicologicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evolucao")
@RequiredArgsConstructor
public class EvolucaoPsicoterapicaController {
    private final EvolucaoPsicologicaService evolucaoPsicologicaService;

    @GetMapping
    public ResponseEntity<List<EvolucaoPsicoterapicaResponse>> buscarTodas() {
        return ResponseEntity.ok(evolucaoPsicologicaService.buscarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvolucaoPsicoterapicaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(evolucaoPsicologicaService.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<EvolucaoPsicoterapicaResponse>> buscarTodasPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(evolucaoPsicologicaService.buscarTodasPorPaciente(pacienteId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EvolucaoPsicoterapicaResponse> editar(@PathVariable UUID id,
                                                                @Valid @RequestBody EvolucaoPsicoterapicaRequest request) {
        return ResponseEntity.ok(evolucaoPsicologicaService.editar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        evolucaoPsicologicaService.remover(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

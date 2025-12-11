package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.PacienteRequest;
import br.com.gestaopsicologica.DTO.responses.PacienteMaxResponse;
import br.com.gestaopsicologica.DTO.responses.PacienteMinResponse;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.mappers.PacienteMapper;
import br.com.gestaopsicologica.repository.PacienteRepository;
import br.com.gestaopsicologica.services.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/paciente")
@RequiredArgsConstructor
public class PacienteController {
    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;

    @GetMapping
    public ResponseEntity<List<PacienteMinResponse>> listarTodosMin() {
        return ResponseEntity.ok(pacienteService.buscarTodosPacientes());
    }

    @GetMapping("/detalhes")
    public ResponseEntity<List<PacienteMaxResponse>> listarTodosMax() {
        return ResponseEntity.ok(pacienteService.buscarTodosPacientesDetalhes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteMinResponse> getByIdMin(@PathVariable UUID id) {
        return  ResponseEntity.ok(pacienteService.buscarPacientePorId(id).orElse(null));
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<PacienteMaxResponse> getByIdMax(@Valid @PathVariable UUID id) {
        return  ResponseEntity.ok(pacienteService.buscarPacientePorIdDetalhes(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<PacienteMinResponse> criar(@Valid @RequestBody PacienteRequest paciente) {
        return ResponseEntity.ok(pacienteService.criarPaciente(paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PacienteMinResponse> deletarPacienteById(@Valid @PathVariable UUID id) {
        pacienteService.removerPaciente(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("{id}/editar")
    public ResponseEntity<PacienteMinResponse> editar(@Valid @RequestBody PacienteRequest paciente, @PathVariable UUID id) {
        return ResponseEntity.ok(pacienteService.atualizarPaciente(id, paciente));
    }

    @PutMapping("{id}/editar/detalhes")
    public ResponseEntity<PacienteMaxResponse> editarDetalhes(@Valid @RequestBody PacienteRequest paciente, @PathVariable UUID id) {
        return ResponseEntity.ok(pacienteService.atualizarPacienteDetalhes(id, paciente));
    }
}

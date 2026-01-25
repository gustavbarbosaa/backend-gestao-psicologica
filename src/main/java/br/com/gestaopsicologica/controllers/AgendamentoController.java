package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.AgendamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.services.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/agendamento")
@RequiredArgsConstructor
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    @GetMapping("/todos")
    public ResponseEntity<List<AgendamentoResponse>> todosAgendamentos() {
        return ResponseEntity.ok(agendamentoService.listarTodosAgendamentos());
    }

    @GetMapping("/por-usuario")
    public ResponseEntity<List<AgendamentoResponse>> buscarAgendamentosPorUsuario() {
        return ResponseEntity.ok(agendamentoService.listarAgendamentosPorUsuario());
    }

    @GetMapping("/por-paciente/{pacienteId}")
    public ResponseEntity<List<AgendamentoResponse>> buscarAgendamentosPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(agendamentoService.listarAgendamentosPorPaciente(pacienteId));
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> criarAgendamento(@Valid @RequestBody AgendamentoRequest agendamentoRequest) {
        return ResponseEntity.ok(agendamentoService.criarAgendamento(agendamentoRequest));
    }

    @PatchMapping("/editar/{agendamentoId}")
    public ResponseEntity<AgendamentoResponse> editarAgendamento(@Valid @RequestBody AgendamentoRequest agendamentoRequest, @PathVariable UUID agendamentoId) {
        return ResponseEntity.ok(agendamentoService.editarAgendamento(agendamentoId, agendamentoRequest));
    }

    @DeleteMapping("/{agendamentoId}")
    public ResponseEntity<Void> deletarAgendamento(@Valid  @PathVariable UUID agendamentoId) {
        agendamentoService.apagarAgendamento(agendamentoId);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

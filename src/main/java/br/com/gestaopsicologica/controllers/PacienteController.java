package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.responses.PacienteMaxResponse;
import br.com.gestaopsicologica.DTO.responses.PacienteMinResponse;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.mappers.PacienteMapper;
import br.com.gestaopsicologica.repository.PacienteRepository;
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
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @GetMapping
    public ResponseEntity<List<PacienteMinResponse>> listarTodosMin() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        List<PacienteMinResponse> responsePacientes = pacienteMapper.toMinResponseList(pacientes);

        return ResponseEntity.ok(responsePacientes);
    }

    @GetMapping("/detalhes")
    public ResponseEntity<List<PacienteMaxResponse>> listarTodosMax() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        List<PacienteMaxResponse> responsePacientes = pacienteMapper.toMaxResponseList(pacientes);

        return ResponseEntity.ok(responsePacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteMinResponse> getByIdMin(@PathVariable UUID id) {
        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        PacienteMinResponse responsePaciente =  pacienteMapper.toMinResponse(paciente);

        return  ResponseEntity.ok(responsePaciente);
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<PacienteMaxResponse> getByIdMax(@PathVariable UUID id) {
        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        PacienteMaxResponse responsePaciente =  pacienteMapper.toMaxResponse(paciente);

        return  ResponseEntity.ok(responsePaciente);
    }

    @PostMapping
    public ResponseEntity<PacienteMinResponse> criar(@RequestBody Paciente paciente) {
        Paciente response = pacienteRepository.save(paciente);

        PacienteMinResponse responsePaciente = pacienteMapper.toMinResponse(response);

        return ResponseEntity.ok(responsePaciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PacienteMinResponse> deletarPacienteById(@PathVariable UUID id) {
        pacienteRepository.deleteById(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

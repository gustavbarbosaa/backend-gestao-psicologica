package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.PacienteRequest;
import br.com.gestaopsicologica.DTO.responses.PacienteMaxResponse;
import br.com.gestaopsicologica.DTO.responses.PacienteMinResponse;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.mappers.PacienteMapper;
import br.com.gestaopsicologica.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    public List<PacienteMinResponse> buscarTodosPacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        return pacienteMapper.toMinResponseList(pacientes);
    }

    public List<PacienteMaxResponse> buscarTodosPacientesDetalhes() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        return pacienteMapper.toMaxResponseList(pacientes);
    }

    public Optional<PacienteMinResponse> buscarPacientePorId(UUID id) {
        return Optional.ofNullable(pacienteRepository.findById(id)
                .map(pacienteMapper::toMinResponse)
                .orElseThrow(() -> new EntityNotFoundException("Paciente com ID " + id + " não encontrado.")));
    }

    public PacienteMaxResponse buscarPacientePorIdDetalhes(UUID id) {
        return pacienteRepository.findById(id)
                .map(pacienteMapper::toMaxResponse)
                .orElseThrow(() -> new EntityNotFoundException("Paciente com ID " + id + " não encontrado."));
    }

    @Transactional
    public PacienteMinResponse criarPaciente (PacienteRequest paciente) {
        Paciente pacienteConvertido = pacienteMapper.toPaciente(paciente);

        pacienteRepository.save(pacienteConvertido);

        return pacienteMapper.toMinResponse(pacienteConvertido);
    }

    @Transactional
    public void removerPaciente(UUID id) {
        if (!pacienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Paciente com ID " + id + " não encontrado para exclusão.");
        }
        pacienteRepository.deleteById(id);
    }

    @Transactional
    public PacienteMinResponse atualizarPaciente(UUID id, PacienteRequest request) {
        Paciente pacienteAtualizado = executarAtualizacao(id, request);
        return pacienteMapper.toMinResponse(pacienteAtualizado);
    }

    @Transactional
    public PacienteMaxResponse atualizarPacienteDetalhes(UUID id, PacienteRequest request) {
        Paciente pacienteAtualizado = executarAtualizacao(id, request);
        return pacienteMapper.toMaxResponse(pacienteAtualizado);
    }

    private Paciente executarAtualizacao(UUID id, PacienteRequest request) {
        if (request == null || id == null) {
            throw new IllegalArgumentException("O ID e os dados do Paciente são obrigatórios.");
        }

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente com ID " + id + " não encontrado."));

        if (request.nome() != null && !request.nome().equals(paciente.getNome())) {
            paciente.setNome(request.nome());
        }

        if (request.email() != null && !request.email().equals(paciente.getEmail())) {
            paciente.setEmail(request.email());
        }

        if (request.telefone() != null && !request.telefone().equals(paciente.getTelefone())) {
            paciente.setTelefone(request.telefone());
        }

        if (request.valorSessaoPadrao() != null && !request.valorSessaoPadrao().equals(paciente.getValorSessaoPadrao())) {
            paciente.setValorSessaoPadrao(request.valorSessaoPadrao());
        }

        return pacienteRepository.save(paciente);
    }
}

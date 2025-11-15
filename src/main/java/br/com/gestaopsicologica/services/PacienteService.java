package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.PacienteRequest;
import br.com.gestaopsicologica.DTO.responses.PacienteMinRespose;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.mappers.PacienteMapper;
import br.com.gestaopsicologica.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    public List<PacienteMinRespose> buscarTodosPacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        return pacienteMapper.toMinResposeList(pacientes);
    }

    public Optional<PacienteMinRespose> buscarPacientePorId(UUID id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);

        return paciente.map(pacienteMapper::toMinRespose);
    }

    public PacienteMinRespose criarPaciente (PacienteRequest paciente) {
        Paciente pacienteConvertido = pacienteMapper.toPaciente(paciente);

        pacienteRepository.save(pacienteConvertido);

        return pacienteMapper.toMinRespose(pacienteConvertido);
    }

    public void removerPaciente(UUID id) {
        pacienteRepository.deleteById(id);
    }

    public PacienteMinRespose atualizarPaciente(UUID id, PacienteRequest paciente) {
        if (paciente == null || id == null) {
            throw new IllegalArgumentException("O ID e os dados do Paciente são obrigatórios.");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente com ID " + id + " não encontrado."));

        if (!pacienteExistente.getNome().equals(paciente.nome())) {
            pacienteExistente.setNome(paciente.nome());
        }

        if (!pacienteExistente.getEmail().equals(paciente.email())) {
            pacienteExistente.setEmail(paciente.email());
        }

        if (!pacienteExistente.getTelefone().equals(paciente.telefone())) {
            pacienteExistente.setTelefone(paciente.telefone());
        }

        if (!pacienteExistente.getValorSessaoPadrao().equals(paciente.valorSessaoPadrao())) {
            pacienteExistente.setValorSessaoPadrao(paciente.valorSessaoPadrao());
        }

        Paciente pacienteAtualizado =  pacienteRepository.save(pacienteExistente);
        return pacienteMapper.toMinRespose(pacienteAtualizado);
    }
}

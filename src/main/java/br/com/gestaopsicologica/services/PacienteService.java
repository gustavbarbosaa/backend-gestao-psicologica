package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.PacienteRequest;
import br.com.gestaopsicologica.DTO.responses.PacienteMaxResponse;
import br.com.gestaopsicologica.DTO.responses.PacienteMinResponse;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.mappers.PacienteMapper;
import br.com.gestaopsicologica.repository.PacienteRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private static final String MENSAGEM_PACIENTE_ID = "Paciente com ID ";
    private static final String NAO_ENCONTRADO = " não encontrado.";

    public List<PacienteMinResponse> buscarTodosPacientes() {
        List<Paciente> pacientes = this.usuarioAutenticadoService.isAdmin()
                ? pacienteRepository.findAll()
                : pacienteRepository.findAllByUsuarioId(this.usuarioAutenticadoService.buscarUsuarioAutenticado());

        return pacienteMapper.toMinResponseList(pacientes);
    }

    public List<PacienteMaxResponse> buscarTodosPacientesDetalhes() {
        List<Paciente> pacientes = this.usuarioAutenticadoService.isAdmin()
                ? pacienteRepository.findAll()
                : pacienteRepository.findAllByUsuarioId(this.usuarioAutenticadoService.buscarUsuarioAutenticado());

        return pacienteMapper.toMaxResponseList(pacientes);
    }

    public Optional<PacienteMinResponse> buscarPacientePorId(UUID id) {
        return Optional.of(findPacienteByScope(id)
                .map(pacienteMapper::toMinResponse)
                .orElseThrow(() -> new EntityNotFoundException(MENSAGEM_PACIENTE_ID + id + NAO_ENCONTRADO)));
    }

    public PacienteMaxResponse buscarPacientePorIdDetalhes(UUID id) {
        return findPacienteByScope(id)
                .map(pacienteMapper::toMaxResponse)
                .orElseThrow(() -> new EntityNotFoundException(MENSAGEM_PACIENTE_ID + id + NAO_ENCONTRADO));
    }

    public List<PacienteMaxResponse> buscarPacientesPorProfissional() {
        return pacienteRepository.findByUsuarioId(this.usuarioAutenticadoService.buscarUsuarioAutenticado());
    }

    @Transactional
    public PacienteMinResponse criarPaciente(PacienteRequest paciente) {
        UUID idUsuario = this.usuarioAutenticadoService.buscarUsuarioAutenticado();

        Usuario usuarioLogado = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado no banco."));

        Paciente novopaciente = new Paciente();
        novopaciente.setNome(paciente.nome());
        novopaciente.setEmail(normalizarEmail(paciente.email()));
        novopaciente.setTelefone(paciente.telefone());

        novopaciente.setUsuario(usuarioLogado);

        pacienteRepository.save(novopaciente);

        return pacienteMapper.toMinResponse(novopaciente);
    }

    @Transactional
    public void removerPaciente(UUID id) {
        Paciente paciente = findPacienteByScope(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        MENSAGEM_PACIENTE_ID + id + " não encontrado para exclusão."
                ));

        paciente.setAtivo(false);
        pacienteRepository.saveAndFlush(paciente);
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

        Paciente paciente = findPacienteByScope(id)
                .orElseThrow(() -> new EntityNotFoundException(MENSAGEM_PACIENTE_ID + id + NAO_ENCONTRADO));

        if (!request.nome().equals(paciente.getNome())) {
            paciente.setNome(request.nome());
        }

        if (request.email() != null && !request.email().equals(paciente.getEmail())) {
            paciente.setEmail(normalizarEmail(request.email()));
        }

        if (request.telefone() != null && !request.telefone().equals(paciente.getTelefone())) {
            paciente.setTelefone(request.telefone());
        }

        return pacienteRepository.save(paciente);
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email;
    }

    private Optional<Paciente> findPacienteByScope(UUID id) {
        if (this.usuarioAutenticadoService.isAdmin()) {
            return pacienteRepository.findById(id);
        }

        return pacienteRepository.findByIdAndUsuarioId(id, this.usuarioAutenticadoService.buscarUsuarioAutenticado());
    }
}

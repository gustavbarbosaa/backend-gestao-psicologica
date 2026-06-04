package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.requests.EvolucaoPsicoterapicaRequest;
import br.com.gestaopsicologica.DTO.responses.EvolucaoPsicoterapicaResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.domain.EvolucaoPsicoterapica;
import br.com.gestaopsicologica.mappers.EvolucaoPsicoterapicaMapper;
import br.com.gestaopsicologica.repository.EvolucaoPsicoterapicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvolucaoPsicologicaService {
    private static final String EVOLUCAO_NAO_ENCONTRADA =
            "Não foi encontrada nenhuma evolução com esta identificação vinculada a este usuário.";

    private final EvolucaoPsicoterapicaRepository evolucaoPsicoterapicaRepository;
    private final EvolucaoPsicoterapicaMapper evolucaoPsicoterapicaMapper;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public List<EvolucaoPsicoterapicaResponse> buscarTodas() {
        List<EvolucaoPsicoterapica> evolucaoPsicoterapicas =
                evolucaoPsicoterapicaRepository.findByAtivoTrueAndAgendamentoUsuarioId(this.usuarioAutenticadoService.buscarUsuarioAutenticado());

        return evolucaoPsicoterapicaMapper.listToDTO(evolucaoPsicoterapicas);
    }

    public EvolucaoPsicoterapicaResponse buscarPorId(UUID evolucaoId) {
        EvolucaoPsicoterapica evolucaoPsicoterapica =
                evolucaoPsicoterapicaRepository.findByIdAndAtivoTrueAndAgendamentoUsuarioId(evolucaoId, this.usuarioAutenticadoService.buscarUsuarioAutenticado())
                        .orElseThrow(() -> new EntityNotFoundException(EVOLUCAO_NAO_ENCONTRADA));

        return evolucaoPsicoterapicaMapper.toDTO(evolucaoPsicoterapica);
    }

    public EvolucaoPsicoterapicaResponse criar(Agendamento agendamento) {
        validarAcessoAoAgendamento(agendamento);

        EvolucaoPsicoterapica evolucaoPsicoterapica = new EvolucaoPsicoterapica();

        evolucaoPsicoterapica.setAgendamento(agendamento);
        evolucaoPsicoterapica.setConteudo(null);
        evolucaoPsicoterapica.setObservacoes(null);

        evolucaoPsicoterapicaRepository.save(evolucaoPsicoterapica);

        return evolucaoPsicoterapicaMapper.toDTO(evolucaoPsicoterapica);
    }

    public EvolucaoPsicoterapicaResponse editar(UUID evolucaoId, EvolucaoPsicoterapicaRequest request) {
        EvolucaoPsicoterapica evolucaoPsicoterapica =
                evolucaoPsicoterapicaRepository.findByIdAndAtivoTrueAndAgendamentoUsuarioId(evolucaoId, this.usuarioAutenticadoService.buscarUsuarioAutenticado())
                        .orElseThrow(() -> new EntityNotFoundException(EVOLUCAO_NAO_ENCONTRADA));

        evolucaoPsicoterapica.setConteudo(request.conteudo());
        evolucaoPsicoterapica.setObservacoes(request.observacoes());
        evolucaoPsicoterapicaRepository.save(evolucaoPsicoterapica);

        return evolucaoPsicoterapicaMapper.toDTO(evolucaoPsicoterapica);
    }

    public void remover(UUID evolucaoId) {
        EvolucaoPsicoterapica evolucaoPsicoterapica =
                evolucaoPsicoterapicaRepository.findByIdAndAtivoTrueAndAgendamentoUsuarioId(evolucaoId, this.usuarioAutenticadoService.buscarUsuarioAutenticado())
                        .orElseThrow(() -> new EntityNotFoundException(EVOLUCAO_NAO_ENCONTRADA));

        evolucaoPsicoterapica.setAtivo(false);
        evolucaoPsicoterapicaRepository.save(evolucaoPsicoterapica);
    }

    public List<EvolucaoPsicoterapicaResponse> buscarTodasPorPaciente(UUID pacienteId) {
        List<EvolucaoPsicoterapica> evolucaoPsicoterapicas =
                evolucaoPsicoterapicaRepository.findByAtivoTrueAndAgendamentoUsuarioIdAndAgendamentoPacienteId(
                        this.usuarioAutenticadoService.buscarUsuarioAutenticado(),
                        pacienteId
                );

        return evolucaoPsicoterapicaMapper.listToDTO(evolucaoPsicoterapicas);
    }

    private void validarAcessoAoAgendamento(Agendamento agendamento) {
        UUID usuarioIdAgendamento = agendamento.getUsuario() != null
                ? agendamento.getUsuario().getId()
                : null;

        if (!this.usuarioAutenticadoService.buscarUsuarioAutenticado().equals(usuarioIdAgendamento)) {
            throw new EntityNotFoundException(EVOLUCAO_NAO_ENCONTRADA);
        }
    }
}

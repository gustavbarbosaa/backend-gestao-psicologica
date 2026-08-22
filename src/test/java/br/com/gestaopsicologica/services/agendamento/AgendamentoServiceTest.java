package br.com.gestaopsicologica.services.agendamento;

import br.com.gestaopsicologica.DTO.requests.AgendamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.domain.Agendamento;
import br.com.gestaopsicologica.domain.Paciente;
import br.com.gestaopsicologica.domain.TipoAtendimento;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import br.com.gestaopsicologica.mappers.AgendamentoMapper;
import br.com.gestaopsicologica.repository.AgendamentoRepository;
import br.com.gestaopsicologica.repository.PacienteRepository;
import br.com.gestaopsicologica.repository.TipoAtendimentoRepository;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import br.com.gestaopsicologica.services.EvolucaoPsicologicaService;
import br.com.gestaopsicologica.services.UsuarioAutenticadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private AgendamentoMapper agendamentoMapper;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoAtendimentoRepository tipoAtendimentoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private EvolucaoPsicologicaService evolucaoPsicologicaService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private UUID usuarioIdValido() {
        return UUID.randomUUID();
    }

    private Usuario usuarioValido(UUID usuarioId) {
        return Usuario.builder()
                .id(usuarioId)
                .nome("Profissional")
                .email("profissional@email.com")
                .senha("123")
                .build();
    }

    private UUID pacienteIdValido() {
        return UUID.randomUUID();
    }

    private Paciente pacienteValido(UUID pacienteId, Usuario usuario) {
        return Paciente.builder()
                .id(pacienteId)
                .nome("Paciente")
                .telefone("83999999999")
                .usuario(usuario)
                .build();
    }

    private UUID tipoAtendimentoIdValido() {
        return UUID.randomUUID();
    }

    private TipoAtendimento tipoAtendimentoValido(UUID tipoAtendimentoId, Usuario usuario) {
        return TipoAtendimento.builder()
                .id(tipoAtendimentoId)
                .usuario(usuario)
                .valorPadraoTipoAtendimento(BigDecimal.valueOf(50))
                .ativo(true)
                .build();
    }

    @Test
    void deveSalvarAgendamentoQuandoDadosForemValidos() {
        UUID agendamentoId = UUID.randomUUID();
        UUID usuarioId = usuarioIdValido();
        UUID pacienteId = pacienteIdValido();
        UUID tipoAtendimentoId = tipoAtendimentoIdValido();
        LocalDateTime inicio = LocalDateTime.of(2030,8,20, 20, 30);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = new AgendamentoRequest(
                inicio,
                60,
                pacienteId,
                tipoAtendimentoId,
                usuarioId
        );

        Agendamento agendamentoMapeado = Agendamento.builder()
                .dataHoraInicio(inicio)
                .duracaoEmMinutos(60)
                .build();

        AgendamentoResponse responseEsperado = new AgendamentoResponse(
                agendamentoId,
                StatusPagamento.PENDENTE,
                inicio,
                inicio.plusMinutes(60),
                null,
                StatusAtendimento.CRIADO,
                null,
                null,
                tipoAtendimento.getValorPadraoTipoAtendimento(),
                true
        );

        when(usuarioAutenticadoService.isAdmin()).thenReturn(false);
        when(usuarioAutenticadoService.buscarUsuarioAutenticado()).thenReturn(usuarioId);
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicio.toLocalDate().atStartOfDay(),
                inicio.toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Collections.emptyList());
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.of(paciente));
        when(tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, usuarioId)).thenReturn(Optional.of(tipoAtendimento));
        when(agendamentoMapper.toEntity(request)).thenReturn(agendamentoMapeado);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendamento = invocation.getArgument(0);
            agendamento.setId(agendamentoId);
            return agendamento;
        });
        when(agendamentoMapper.toResponse(any(Agendamento.class))).thenReturn(responseEsperado);

        AgendamentoResponse response = agendamentoService.criarAgendamento(request);

        ArgumentCaptor<Agendamento> agendamentoCaptor = ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(agendamentoCaptor.capture());
        verify(evolucaoPsicologicaService).criar(agendamentoCaptor.getValue());

        Agendamento agendamentoSalvo = agendamentoCaptor.getValue();
        assertEquals(inicio, agendamentoSalvo.getDataHoraInicio());
        assertEquals(60, agendamentoSalvo.getDuracaoEmMinutos());
        assertSame(usuario, agendamentoSalvo.getUsuario());
        assertSame(paciente, agendamentoSalvo.getPaciente());
        assertSame(tipoAtendimento, agendamentoSalvo.getTipoAtendimento());
        assertEquals(StatusAtendimento.CRIADO, agendamentoSalvo.getStatusAtendimento());
        assertEquals(StatusPagamento.PENDENTE, agendamentoSalvo.getStatusPagamento());
        assertEquals(tipoAtendimento.getValorPadraoTipoAtendimento(), agendamentoSalvo.getValorAtendimento());

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void naoDeveSalvarAgendamentoEmCasoDeConflitoDeHorario() {
        UUID usuarioId = usuarioIdValido();
        UUID pacienteId = pacienteIdValido();
        UUID tipoAtendimentoId = tipoAtendimentoIdValido();
        LocalDateTime inicio = LocalDateTime.of(2030,8,20, 20, 30);
        LocalDateTime inicioNovoAgendamento = LocalDateTime.of(2030, 8, 20, 21, 0);


        AgendamentoRequest novoAgendamentoConflito = new AgendamentoRequest(
                inicioNovoAgendamento,
                60,
                pacienteId,
                tipoAtendimentoId,
                usuarioId
        );

        Agendamento agendamentoExistenteMapeado = Agendamento.builder()
                .id(UUID.randomUUID())
                .dataHoraInicio(inicio)
                .duracaoEmMinutos(60)
                .ativo(true)
                .build();

        when(usuarioAutenticadoService.isAdmin()).thenReturn(false);
        when(usuarioAutenticadoService.buscarUsuarioAutenticado()).thenReturn(usuarioId);
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicioNovoAgendamento.toLocalDate().atStartOfDay(),
                inicioNovoAgendamento.toLocalDate().atTime(LocalTime.MAX)
        )).thenReturn(List.of(agendamentoExistenteMapeado));

        String mensagemEsperada =
                "Conflito de horário! Já existe agendamento das " +
                        inicio.toLocalTime() + " às " + inicio.plusMinutes(60).toLocalTime();


       IllegalArgumentException exception = assertThrows(
               IllegalArgumentException.class,
               () -> agendamentoService.criarAgendamento(novoAgendamentoConflito)
       );

       assertEquals(mensagemEsperada, exception.getMessage());

       verify(agendamentoRepository, never()).save(any());
       verify(evolucaoPsicologicaService, never()).criar(any());
    }

    @Test
    void deveCriarAgendamentoQuandoConflitoForComAgendamentoInativo() {
        UUID agendamentoId = UUID.randomUUID();
        UUID usuarioId = usuarioIdValido();
        UUID pacienteId = pacienteIdValido();
        UUID tipoAtendimentoId = tipoAtendimentoIdValido();
        LocalDateTime inicioAgendamentoExistente = LocalDateTime.of(2030, 8, 20, 20, 30);
        LocalDateTime inicioNovoAgendamento = LocalDateTime.of(2030, 8, 20, 21, 0);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = new AgendamentoRequest(
                inicioNovoAgendamento,
                60,
                pacienteId,
                tipoAtendimentoId,
                usuarioId
        );

        Agendamento agendamentoMapeado = Agendamento.builder()
                .dataHoraInicio(inicioNovoAgendamento)
                .duracaoEmMinutos(60)
                .build();

        AgendamentoResponse responseEsperado = new AgendamentoResponse(
                agendamentoId,
                StatusPagamento.PENDENTE,
                inicioNovoAgendamento,
                inicioNovoAgendamento.plusMinutes(60),
                null,
                StatusAtendimento.CRIADO,
                null,
                null,
                tipoAtendimento.getValorPadraoTipoAtendimento(),
                true
        );

        Agendamento agendamentoExistenteMapeado = Agendamento.builder()
                .id(UUID.randomUUID())
                .dataHoraInicio(inicioAgendamentoExistente)
                .duracaoEmMinutos(60)
                .ativo(false)
                .build();

        when(usuarioAutenticadoService.isAdmin()).thenReturn(false);
        when(usuarioAutenticadoService.buscarUsuarioAutenticado()).thenReturn(usuarioId);
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicioNovoAgendamento.toLocalDate().atStartOfDay(),
                inicioNovoAgendamento.toLocalDate().atTime(LocalTime.MAX)
        )).thenReturn(List.of(agendamentoExistenteMapeado));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.of(paciente));
        when(tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, usuarioId)).thenReturn(Optional.of(tipoAtendimento));
        when(agendamentoMapper.toEntity(request)).thenReturn(agendamentoMapeado);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendamento = invocation.getArgument(0);
            agendamento.setId(agendamentoId);
            return agendamento;
        });
        when(agendamentoMapper.toResponse(any(Agendamento.class))).thenReturn(responseEsperado);

        AgendamentoResponse response = agendamentoService.criarAgendamento(request);

        ArgumentCaptor<Agendamento> agendamentoCaptor = ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(agendamentoCaptor.capture());
        verify(evolucaoPsicologicaService).criar(agendamentoCaptor.getValue());

        Agendamento agendamentoSalvo = agendamentoCaptor.getValue();
        assertEquals(inicioNovoAgendamento, agendamentoSalvo.getDataHoraInicio());
        assertEquals(60, agendamentoSalvo.getDuracaoEmMinutos());
        assertEquals(inicioNovoAgendamento.plusMinutes(60), agendamentoSalvo.getDataHoraFim());
        assertSame(usuario, agendamentoSalvo.getUsuario());
        assertSame(paciente, agendamentoSalvo.getPaciente());
        assertSame(tipoAtendimento, agendamentoSalvo.getTipoAtendimento());
        assertEquals(StatusAtendimento.CRIADO, agendamentoSalvo.getStatusAtendimento());
        assertEquals(StatusPagamento.PENDENTE, agendamentoSalvo.getStatusPagamento());
        assertEquals(tipoAtendimento.getValorPadraoTipoAtendimento(), agendamentoSalvo.getValorAtendimento());

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void deveCriarAgendamentoQuandoNovoHorarioComecarNoFimDoExistent() {
        UUID agendamentoId = UUID.randomUUID();
        UUID usuarioId = usuarioIdValido();
        UUID pacienteId = pacienteIdValido();
        UUID tipoAtendimentoId = tipoAtendimentoIdValido();
        LocalDateTime inicioAgendamentoExistente = LocalDateTime.of(2030, 8, 20, 20, 30);
        LocalDateTime inicioNovoAgendamento = LocalDateTime.of(2030, 8, 20, 21, 30);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = new AgendamentoRequest(
                inicioNovoAgendamento,
                60,
                pacienteId,
                tipoAtendimentoId,
                usuarioId
        );

        Agendamento agendamentoMapeado = Agendamento.builder()
                .dataHoraInicio(inicioNovoAgendamento)
                .duracaoEmMinutos(60)
                .build();

        Agendamento agendamentoExistenteMapeado = Agendamento.builder()
                .id(UUID.randomUUID())
                .dataHoraInicio(inicioAgendamentoExistente)
                .duracaoEmMinutos(60)
                .ativo(true)
                .build();

        AgendamentoResponse responseEsperado = new AgendamentoResponse(
                agendamentoId,
                StatusPagamento.PENDENTE,
                inicioNovoAgendamento,
                inicioNovoAgendamento.plusMinutes(60),
                null,
                StatusAtendimento.CRIADO,
                null,
                null,
                tipoAtendimento.getValorPadraoTipoAtendimento(),
                true
        );

        when(usuarioAutenticadoService.isAdmin()).thenReturn(false);
        when(usuarioAutenticadoService.buscarUsuarioAutenticado()).thenReturn(usuarioId);
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicioNovoAgendamento.toLocalDate().atStartOfDay(),
                inicioNovoAgendamento.toLocalDate().atTime(LocalTime.MAX)
        )).thenReturn(List.of(agendamentoExistenteMapeado));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.of(paciente));
        when(tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, usuarioId)).thenReturn(Optional.of(tipoAtendimento));
        when(agendamentoMapper.toEntity(request)).thenReturn(agendamentoMapeado);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendamento = invocation.getArgument(0);
            agendamento.setId(agendamentoId);
            return agendamento;
        });
        when(agendamentoMapper.toResponse(any(Agendamento.class))).thenReturn(responseEsperado);

        AgendamentoResponse response = agendamentoService.criarAgendamento(request);

        ArgumentCaptor<Agendamento> agendamentoCaptor = ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(agendamentoCaptor.capture());
        verify(evolucaoPsicologicaService).criar(agendamentoCaptor.getValue());

        Agendamento agendamentoSalvo = agendamentoCaptor.getValue();
        assertEquals(inicioNovoAgendamento, agendamentoSalvo.getDataHoraInicio());
        assertEquals(60, agendamentoSalvo.getDuracaoEmMinutos());
        assertEquals(inicioNovoAgendamento.plusMinutes(60), agendamentoSalvo.getDataHoraFim());
        assertSame(usuario, agendamentoSalvo.getUsuario());
        assertSame(paciente, agendamentoSalvo.getPaciente());
        assertSame(tipoAtendimento, agendamentoSalvo.getTipoAtendimento());
        assertEquals(StatusAtendimento.CRIADO, agendamentoSalvo.getStatusAtendimento());
        assertEquals(StatusPagamento.PENDENTE, agendamentoSalvo.getStatusPagamento());
        assertEquals(tipoAtendimento.getValorPadraoTipoAtendimento(), agendamentoSalvo.getValorAtendimento());

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }
}

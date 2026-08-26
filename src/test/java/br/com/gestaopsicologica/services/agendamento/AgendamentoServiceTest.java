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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
    private UUID usuarioId;
    private UUID pacienteId;
    private UUID tipoAtendimentoId;
    private static final LocalDateTime INICIO_PADRAO =
            LocalDateTime.of(2030, 8, 20, 20, 30);

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

    private AgendamentoRequest criarRequestValido(
            LocalDateTime inicio,
            UUID pacienteId,
            UUID tipoAtendimentoId,
            UUID usuarioId,
            StatusPagamento statusPagamento
    ) {
        return new AgendamentoRequest(
                inicio,
                60,
                pacienteId,
                tipoAtendimentoId,
                usuarioId,
                statusPagamento
        );
    }

    private Agendamento criarAgendamentoMapeado(LocalDateTime inicio) {
        return Agendamento.builder()
                .dataHoraInicio(inicio)
                .duracaoEmMinutos(60)
                .build();
    }

    private Agendamento criarAgendamentoExistente(
            LocalDateTime inicio,
            boolean ativo
    ) {
        return Agendamento.builder()
                .id(UUID.randomUUID())
                .dataHoraInicio(inicio)
                .duracaoEmMinutos(60)
                .ativo(ativo)
                .build();
    }

    private AgendamentoResponse criarResponseEsperado(UUID agendamentoId, LocalDateTime inicio, TipoAtendimento tipoAtendimento) {
        return new AgendamentoResponse(
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
    }

    private void assertAgendamentoCriadoCorretamente(
            Agendamento agendamentoSalvo,
            LocalDateTime inicio,
            Usuario usuario,
            Paciente paciente,
            TipoAtendimento tipoAtendimento
    ) {
        assertEquals(inicio, agendamentoSalvo.getDataHoraInicio());
        assertEquals(60, agendamentoSalvo.getDuracaoEmMinutos());
        assertEquals(inicio.plusMinutes(60), agendamentoSalvo.getDataHoraFim());
        assertSame(usuario, agendamentoSalvo.getUsuario());
        assertSame(paciente, agendamentoSalvo.getPaciente());
        assertSame(tipoAtendimento, agendamentoSalvo.getTipoAtendimento());
        assertEquals(StatusAtendimento.CRIADO, agendamentoSalvo.getStatusAtendimento());
        assertEquals(StatusPagamento.PENDENTE, agendamentoSalvo.getStatusPagamento());
        assertEquals(tipoAtendimento.getValorPadraoTipoAtendimento(), agendamentoSalvo.getValorAtendimento());
    }

    private void configurarProfissionalAutenticado(UUID usuarioId) {
        when(usuarioAutenticadoService.isAdmin()).thenReturn(false);
        when(usuarioAutenticadoService.buscarUsuarioAutenticado()).thenReturn(usuarioId);
    }

    private void configurarAgendaSemConflitos(UUID usuarioId, LocalDateTime inicio) {
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicio.toLocalDate().atStartOfDay(),
                inicio.toLocalDate().atTime(LocalTime.MAX)
        )).thenReturn(Collections.emptyList());
    }

    @BeforeEach
    void configurarDadosBase() {
        usuarioId = usuarioIdValido();
        pacienteId = pacienteIdValido();
        tipoAtendimentoId = tipoAtendimentoIdValido();
    }

    @Test
    void deveSalvarAgendamentoQuandoDadosForemValidos() {
        UUID agendamentoId = UUID.randomUUID();

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = criarRequestValido(INICIO_PADRAO, pacienteId, tipoAtendimentoId, usuarioId, null);

        Agendamento agendamentoMapeado = criarAgendamentoMapeado(INICIO_PADRAO);

        AgendamentoResponse responseEsperado = criarResponseEsperado(agendamentoId, INICIO_PADRAO, tipoAtendimento);

        configurarProfissionalAutenticado(usuarioId);
        configurarAgendaSemConflitos(usuarioId, INICIO_PADRAO);
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

        assertAgendamentoCriadoCorretamente(agendamentoCaptor.getValue(), INICIO_PADRAO, usuario, paciente, tipoAtendimento);

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void naoDeveSalvarAgendamentoEmCasoDeConflitoDeHorario() {
        LocalDateTime inicioNovoAgendamento = LocalDateTime.of(2030, 8, 20, 21, 0);

        AgendamentoRequest novoAgendamentoConflito = criarRequestValido(inicioNovoAgendamento, pacienteId, tipoAtendimentoId, usuarioId, null);

        Agendamento agendamentoExistenteMapeado = criarAgendamentoExistente(INICIO_PADRAO, true);

        configurarProfissionalAutenticado(usuarioId);
        when(agendamentoRepository.findAgendamentosByUsuarioIdAndDataHoraInicioBetween(
                usuarioId,
                inicioNovoAgendamento.toLocalDate().atStartOfDay(),
                inicioNovoAgendamento.toLocalDate().atTime(LocalTime.MAX)
        )).thenReturn(List.of(agendamentoExistenteMapeado));

        String mensagemEsperada =
                "Conflito de horário! Já existe agendamento das " +
                        INICIO_PADRAO.toLocalTime() + " às " + INICIO_PADRAO.plusMinutes(60).toLocalTime();


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
        LocalDateTime inicioAgendamentoExistente = LocalDateTime.of(2030, 8, 20, 20, 30);
        LocalDateTime inicioNovoAgendamento = LocalDateTime.of(2030, 8, 20, 21, 0);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = criarRequestValido(inicioNovoAgendamento, pacienteId, tipoAtendimentoId, usuarioId, null);

        Agendamento agendamentoMapeado = criarAgendamentoMapeado(inicioNovoAgendamento);

        AgendamentoResponse responseEsperado = criarResponseEsperado(agendamentoId, inicioNovoAgendamento, tipoAtendimento);

        Agendamento agendamentoExistenteMapeado = criarAgendamentoExistente(inicioAgendamentoExistente, false);

        configurarProfissionalAutenticado(usuarioId);
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

        assertAgendamentoCriadoCorretamente(agendamentoCaptor.getValue(), inicioNovoAgendamento, usuario, paciente, tipoAtendimento);

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void deveCriarAgendamentoQuandoNovoHorarioComecarNoFimDoExistente() {
        UUID agendamentoId = UUID.randomUUID();
        LocalDateTime inicioAgendamentoExistente = LocalDateTime.of(2030, 8, 20, 20, 30);
        LocalDateTime inicioNovoAgendamento = inicioAgendamentoExistente.plusMinutes(60);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = criarRequestValido(inicioNovoAgendamento, pacienteId, tipoAtendimentoId, usuarioId, null);

        Agendamento agendamentoMapeado = criarAgendamentoMapeado(inicioNovoAgendamento);

        Agendamento agendamentoExistenteMapeado = criarAgendamentoExistente(inicioAgendamentoExistente, true);

        AgendamentoResponse responseEsperado = criarResponseEsperado(agendamentoId, inicioNovoAgendamento, tipoAtendimento);

        configurarProfissionalAutenticado(usuarioId);
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

        assertAgendamentoCriadoCorretamente(agendamentoCaptor.getValue(), inicioNovoAgendamento, usuario, paciente, tipoAtendimento);

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void deveCriarAgendamentoQuandoNovoHorarioTerminarNoInicioDoExistente() {
        UUID agendamentoId = UUID.randomUUID();
        LocalDateTime inicioAgendamentoExistente = LocalDateTime.of(2030, 8, 20, 20, 30);
        LocalDateTime inicioNovoAgendamento = inicioAgendamentoExistente.minusMinutes(60);

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = criarRequestValido(inicioNovoAgendamento, pacienteId, tipoAtendimentoId, usuarioId, null);

        Agendamento agendamentoMapeado = criarAgendamentoMapeado(inicioNovoAgendamento);

        Agendamento agendamentoExistenteMapeado = criarAgendamentoExistente(inicioAgendamentoExistente, true);

        AgendamentoResponse responseEsperado = criarResponseEsperado(agendamentoId, inicioNovoAgendamento, tipoAtendimento);

        configurarProfissionalAutenticado(usuarioId);
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

        assertAgendamentoCriadoCorretamente(agendamentoCaptor.getValue(), inicioNovoAgendamento, usuario, paciente, tipoAtendimento);

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }

    @Test
    void naoDeveCriarAgendamentoCasoNaoEncontrePaciente() {
        Usuario usuario = usuarioValido(usuarioId);

        AgendamentoRequest novoAgendamento = criarRequestValido(INICIO_PADRAO, pacienteId, tipoAtendimentoId, usuarioId, null);

        configurarProfissionalAutenticado(usuarioId);
        configurarAgendaSemConflitos(usuarioId, INICIO_PADRAO);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.empty());

        String mensagemEsperada = "Paciente não encontrado";

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> agendamentoService.criarAgendamento(novoAgendamento)
        );

        assertEquals(mensagemEsperada, exception.getMessage());

        verifyNoInteractions(tipoAtendimentoRepository);
        verifyNoInteractions(agendamentoMapper);
        verify(agendamentoRepository, never()).save(any());
        verify(evolucaoPsicologicaService, never()).criar(any());
    }

    @Test
    void naoDeveCriarAgendamentoCasoNaoEncontreUsuario() {
        AgendamentoRequest novoAgendamento = criarRequestValido(INICIO_PADRAO, pacienteId, tipoAtendimentoId, usuarioId, null);

        configurarProfissionalAutenticado(usuarioId);
        configurarAgendaSemConflitos(usuarioId, INICIO_PADRAO);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        String mensagemEsperada = "Usuário não encontrado";

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> agendamentoService.criarAgendamento(novoAgendamento)
        );

        assertEquals(mensagemEsperada, exception.getMessage());

        verifyNoInteractions(tipoAtendimentoRepository);
        verifyNoInteractions(pacienteRepository);
        verifyNoInteractions(agendamentoMapper);
        verify(agendamentoRepository, never()).save(any());
        verify(evolucaoPsicologicaService, never()).criar(any());
    }

    @Test
    void naoDeveCriarAgendamentoCasoNaoEncontreTipoAtendimento() {
        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);

        AgendamentoRequest novoAgendamento = criarRequestValido(INICIO_PADRAO, pacienteId, tipoAtendimentoId, usuarioId, null);

        configurarProfissionalAutenticado(usuarioId);
        configurarAgendaSemConflitos(usuarioId, INICIO_PADRAO);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.of(paciente));
        when(tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, usuarioId)).thenReturn(Optional.empty());

        String mensagemEsperada = "Tipo de atendimento não encontrado";

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> agendamentoService.criarAgendamento(novoAgendamento)
        );

        assertEquals(mensagemEsperada, exception.getMessage());

        verifyNoInteractions(agendamentoMapper);
        verify(agendamentoRepository, never()).save(any());
        verify(evolucaoPsicologicaService, never()).criar(any());
    }

    @Test
    void deveCriarAgendamentoPreservandoStatusDePagamento() {
        UUID agendamentoId = UUID.randomUUID();

        Usuario usuario = usuarioValido(usuarioId);
        Paciente paciente = pacienteValido(pacienteId, usuario);
        TipoAtendimento tipoAtendimento = tipoAtendimentoValido(tipoAtendimentoId, usuario);

        AgendamentoRequest request = criarRequestValido(INICIO_PADRAO, pacienteId, tipoAtendimentoId, usuarioId, StatusPagamento.CONFIRMADO);

        Agendamento agendamentoMapeado = Agendamento.builder()
                .dataHoraInicio(INICIO_PADRAO)
                .statusPagamento(StatusPagamento.CONFIRMADO)
                .duracaoEmMinutos(60)
                .build();

        AgendamentoResponse responseEsperado = new AgendamentoResponse(
                agendamentoId,
                StatusPagamento.CONFIRMADO,
                INICIO_PADRAO,
                INICIO_PADRAO.plusMinutes(60),
                null,
                StatusAtendimento.CRIADO,
                null,
                null,
                tipoAtendimento.getValorPadraoTipoAtendimento(),
                true
        );

        configurarProfissionalAutenticado(usuarioId);
        configurarAgendaSemConflitos(usuarioId, INICIO_PADRAO);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findByIdAndUsuarioId(pacienteId, usuarioId)).thenReturn(Optional.of(paciente));
        when(tipoAtendimentoRepository.findByIdAndUsuarioId(tipoAtendimentoId, usuarioId)).thenReturn(Optional.of(tipoAtendimento));
        when(agendamentoMapper.toEntity(request)).thenReturn(agendamentoMapeado);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento agendendamento = invocation.getArgument(0);
            agendendamento.setId(agendamentoId);
            return agendendamento;
        });
        when(agendamentoMapper.toResponse(agendamentoMapeado)).thenReturn(responseEsperado);

        AgendamentoResponse response = agendamentoService.criarAgendamento(request);

        ArgumentCaptor<Agendamento> argumentCaptor = ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(argumentCaptor.capture());
        verify(evolucaoPsicologicaService).criar(argumentCaptor.getValue());

        assertEquals(INICIO_PADRAO, argumentCaptor.getValue().getDataHoraInicio());
        assertEquals(60, argumentCaptor.getValue().getDuracaoEmMinutos());
        assertEquals(INICIO_PADRAO.plusMinutes(60), argumentCaptor.getValue().getDataHoraFim());
        assertSame(usuario, argumentCaptor.getValue().getUsuario());
        assertSame(paciente, argumentCaptor.getValue().getPaciente());
        assertSame(tipoAtendimento, argumentCaptor.getValue().getTipoAtendimento());
        assertEquals(StatusAtendimento.CRIADO, argumentCaptor.getValue().getStatusAtendimento());
        assertEquals(StatusPagamento.CONFIRMADO, argumentCaptor.getValue().getStatusPagamento());
        assertEquals(tipoAtendimento.getValorPadraoTipoAtendimento(), argumentCaptor.getValue().getValorAtendimento());

        assertNotNull(response);
        assertEquals(responseEsperado, response);
    }
}

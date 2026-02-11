package br.com.gestaopsicologica.DTO.responses;

import br.com.gestaopsicologica.enums.StatusPagamento;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id,
        StatusPagamento statusPagamento,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        PacienteMinResponse paciente,
        TipoAtendimentoResponse tipoAtendimento,
        UsuarioResponse usuario
) {}

package br.com.gestaopsicologica.DTO.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequest(
        @NotNull(message = "A data e hora de início são obrigatórias")
        @Future(message = "O agendamento deve ser numa data futura")
        LocalDateTime dataHoraInicio,
        @NotNull(message = "A duração do atendimento é obrigatória")
        @Positive(message = "A duração precisa ser maior que 0")
        Integer duracaoEmMinutos,
        @NotNull(message = "O paciente é obrigatório para o agendamento")
        UUID pacienteId,
        @NotNull(message = "O tipo de atendimento e obrigatório")
        UUID tipoAtendimentoId,
        @NotNull(message = "O profissional é obrigatório")
        UUID usuarioId
) {}

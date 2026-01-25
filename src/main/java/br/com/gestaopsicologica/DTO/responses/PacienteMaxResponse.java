package br.com.gestaopsicologica.DTO.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record PacienteMaxResponse(UUID id, String nome, String email, String telefone, BigDecimal valorSessaoPadrao, Boolean ativo) {
}

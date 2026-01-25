package br.com.gestaopsicologica.DTO.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record PacienteMinResponse(UUID id, String nome, BigDecimal valorSessaoPadrao, Boolean ativo) {
}

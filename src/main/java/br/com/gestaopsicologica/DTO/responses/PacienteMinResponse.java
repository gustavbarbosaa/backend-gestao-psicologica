package br.com.gestaopsicologica.DTO.responses;

import java.math.BigDecimal;

public record PacienteMinResponse(String nome, BigDecimal valorSessaoPadrao) {
}

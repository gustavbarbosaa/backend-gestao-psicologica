package br.com.gestaopsicologica.DTO.responses;

import java.math.BigDecimal;

public record PacienteMaxResponse(String nome, String email, String telefone, BigDecimal valorSessaoPadrao) {
}

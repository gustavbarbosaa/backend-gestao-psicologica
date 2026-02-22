package br.com.gestaopsicologica.DTO.responses;

import br.com.gestaopsicologica.enums.TipoAtendimentoEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoAtendimentoResponse(UUID id, TipoAtendimentoEnum nome, BigDecimal valorPadraoTipoAtendimento) {
}

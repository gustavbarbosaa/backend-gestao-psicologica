package br.com.gestaopsicologica.DTO.responses;

import br.com.gestaopsicologica.enums.TipoAtendimentoEnum;

import java.math.BigDecimal;

public record TipoAtendimentoResponse(TipoAtendimentoEnum nome, BigDecimal valorPadraoTipoAtendimento) {
}

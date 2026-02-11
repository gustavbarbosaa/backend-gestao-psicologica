package br.com.gestaopsicologica.DTO.requests;

import br.com.gestaopsicologica.enums.TipoAtendimentoEnum;

import java.math.BigDecimal;

public record TipoAtendimentoRequest(TipoAtendimentoEnum tipoAtendimento, BigDecimal valorPadraoTipoAtendimento) {
}

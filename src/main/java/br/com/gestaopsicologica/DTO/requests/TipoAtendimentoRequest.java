package br.com.gestaopsicologica.DTO.requests;

import br.com.gestaopsicologica.enums.TipoAtendimentoEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoAtendimentoRequest(
        TipoAtendimentoEnum nome,
        BigDecimal valorPadraoTipoAtendimento,
        UUID usuarioId
) {
}

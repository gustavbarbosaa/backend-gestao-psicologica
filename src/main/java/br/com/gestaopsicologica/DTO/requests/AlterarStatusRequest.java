package br.com.gestaopsicologica.DTO.requests;

import br.com.gestaopsicologica.enums.StatusAtendimento;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest(@NotNull StatusAtendimento statusAtendimento) {
}

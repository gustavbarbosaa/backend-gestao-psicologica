package br.com.gestaopsicologica.config;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUsuarioData(UUID id, String email) {
}

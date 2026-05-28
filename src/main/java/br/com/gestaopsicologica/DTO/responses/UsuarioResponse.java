package br.com.gestaopsicologica.DTO.responses;

import br.com.gestaopsicologica.domain.Usuario;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String email, Boolean ativo) {
    public UsuarioResponse(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAtivo());
    }
}

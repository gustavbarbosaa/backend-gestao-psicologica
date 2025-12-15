package br.com.gestaopsicologica.DTO.responses;

import br.com.gestaopsicologica.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String email, List<String> permissoes) {
    public UsuarioResponse(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}

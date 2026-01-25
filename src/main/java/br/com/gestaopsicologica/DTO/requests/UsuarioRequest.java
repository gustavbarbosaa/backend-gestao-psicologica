package br.com.gestaopsicologica.DTO.requests;

import br.com.gestaopsicologica.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

public record UsuarioRequest(UUID id, String nome, String email, List<String> permissoes) {
    public UsuarioRequest(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}

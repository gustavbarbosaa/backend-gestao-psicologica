package br.com.gestaopsicologica.services;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioAutenticadoService {
    public UUID buscarUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "PAPEL_ADMIN".equals(authority.getAuthority()));
    }

    public UUID resolveTargetUsuarioId(UUID requestedUsuarioId) {
        if (isAdmin() && requestedUsuarioId != null) {
            return requestedUsuarioId;
        }

        return this.buscarUsuarioAutenticado();
    }
}

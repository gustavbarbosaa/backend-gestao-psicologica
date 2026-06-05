package br.com.gestaopsicologica.services;

import br.com.gestaopsicologica.DTO.responses.LoginResponse;
import br.com.gestaopsicologica.DTO.responses.UsuarioResponse;
import br.com.gestaopsicologica.config.TokenConfig;
import br.com.gestaopsicologica.domain.Usuario;
import br.com.gestaopsicologica.enums.AuthProvider;
import br.com.gestaopsicologica.mappers.UsuarioMapper;
import br.com.gestaopsicologica.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final TokenConfig tokenConfig;

    @Value("${google.cliente-id}")
    private String googleClientId;

    public LoginResponse login(String idToken) {
        GoogleIdToken.Payload payload = validarTokenGoogle(idToken);

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        Boolean emailVerificado = payload.getEmailVerified();
        String nome = (String) payload.get("name");
        String fotoUrl = (String) payload.get("picture");

        if (!Boolean.TRUE.equals(emailVerificado)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Email do Google não verificado.");
        }

        Usuario usuario = buscarUsuarioGoogle(googleId, email);

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(CONFLICT, "Usuário inativo.");
        }

        atualizarDadosDoUsuario(usuario, nome, fotoUrl);

        String token = tokenConfig.geraToken(usuario);
        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);

        return new LoginResponse(
                token,
                usuarioResponse
        );
    }

    private GoogleIdToken.Payload validarTokenGoogle(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new ResponseStatusException(UNAUTHORIZED, "Token Google inválido.");
            }

            return googleIdToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Erro ao validar token do Google.", e);
        }
    }

    Usuario buscarUsuarioGoogle(String googleId, String email) {
        return usuarioRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, googleId)
                .orElseGet(() -> vincularUsuarioExistenteOuRejeitar(googleId, email));
    }

    private Usuario vincularUsuarioExistenteOuRejeitar(String googleId, String email) {
        return usuarioRepository.findUsuarioByEmail(email)
                .map(usuario -> vincularContaGoogle(usuario, googleId))
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuário não cadastrado para login com Google."));
    }

    private Usuario vincularContaGoogle(Usuario usuario, String googleId) {
        if (usuario.getProvider() == AuthProvider.GOOGLE) {
            if (googleId.equals(usuario.getProviderId())) {
                return usuario;
            }

            if (usuario.getProviderId() != null && !usuario.getProviderId().isBlank()) {
                throw new ResponseStatusException(CONFLICT, "Esta conta já está vinculada a outro login Google.");
            }
        }

        usuario.setProvider(AuthProvider.GOOGLE);
        usuario.setProviderId(googleId);
        return usuarioRepository.save(usuario);
    }

    private void atualizarDadosDoUsuario(Usuario usuario, String nome, String fotoUrl) {
        boolean alterou = false;

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            usuario.setNome(nome);
            alterou = true;
        }

        if (fotoUrl != null && !fotoUrl.isBlank() && !fotoUrl.equals(usuario.getFotoUrl())) {
            usuario.setFotoUrl(fotoUrl);
            alterou = true;
        }

        if (alterou) {
            usuarioRepository.save(usuario);
        }
    }
}

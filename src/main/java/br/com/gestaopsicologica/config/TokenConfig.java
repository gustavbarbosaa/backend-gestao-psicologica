package br.com.gestaopsicologica.config;

import br.com.gestaopsicologica.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenConfig {
    @Value("${app.secret}")
    private String secret;

    public String geraToken(Usuario usuario){

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("id", usuario.getId().toString())
                .withSubject(usuario.getEmail())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(86400)))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUsuarioData> validaToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

            return Optional.of(JWTUsuarioData.builder()
                    .id(UUID.fromString(decodedJWT.getClaim("id").asString()))
                    .email(decodedJWT.getSubject())
                    .build());
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}

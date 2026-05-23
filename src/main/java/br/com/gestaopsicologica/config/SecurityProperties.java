package br.com.gestaopsicologica.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(Cors cors, Cookie cookie) {

    public record Cors(List<String> allowedOrigins) {
    }

    public record Cookie(
            boolean secure,
            String sameSite,
            long maxAge
    ) {
    }
}

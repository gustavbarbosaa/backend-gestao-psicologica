package br.com.gestaopsicologica.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(Cookie cookie) {

    public record Cookie(
            boolean secure,
            String sameSite,
            long maxAge
    ) {
    }
}

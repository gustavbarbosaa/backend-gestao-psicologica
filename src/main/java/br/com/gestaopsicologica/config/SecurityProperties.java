package br.com.gestaopsicologica.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(Cors cors, Cookie cookie) {

    public SecurityProperties {
        cors = cors != null ? cors : new Cors(List.of());
        cookie = cookie != null ? cookie : new Cookie(false, "Lax", 86400, null);
    }

    public record Cors(List<String> allowedOrigins) {
        public Cors {
            allowedOrigins = allowedOrigins != null ? List.copyOf(allowedOrigins) : List.of();
        }
    }

    public record Cookie(
            boolean secure,
            String sameSite,
            long maxAge,
            String domain
    ) {
        public Cookie {
            sameSite = sameSite != null ? sameSite : "Lax";
            maxAge = maxAge > 0 ? maxAge : 86400;
            domain = domain != null && !domain.isBlank() ? domain.trim() : null;
        }
    }
}

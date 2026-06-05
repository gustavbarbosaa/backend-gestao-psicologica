package br.com.gestaopsicologica.security;

import br.com.gestaopsicologica.config.SecurityFilter;
import br.com.gestaopsicologica.config.SecurityProperties;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final SecurityFilter securityFilter;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                "/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/v1/autenticacao/login",
                                "/api/v1/autenticacao/google",
                                "/api/v1/autenticacao/cadastro",
                                "/api/v1/autenticacao/logout"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/autenticacao/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/autenticacao/google").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/autenticacao/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/autenticacao/cadastro").hasAuthority("USUARIO_CADASTRAR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/paciente/**").hasAuthority("PACIENTE_VISUALIZAR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/paciente").hasAuthority("PACIENTE_CRIAR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/paciente/**").hasAuthority("PACIENTE_EDITAR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/paciente/**").hasAuthority("PACIENTE_REMOVER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/agendamento/todos").hasAuthority("AGENDAMENTO_VISUALIZAR_TODOS")
                        .requestMatchers(HttpMethod.GET, "/api/v1/agendamento/todos/incluindo-inativos").hasAuthority("AGENDAMENTO_VISUALIZAR_TODOS")
                        .requestMatchers(HttpMethod.GET, "/api/v1/agendamento/**").hasAuthority("AGENDAMENTO_VISUALIZAR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/agendamento").hasAuthority("AGENDAMENTO_CRIAR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/agendamento/editar/**").hasAuthority("AGENDAMENTO_EDITAR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/agendamento/**").hasAuthority("AGENDAMENTO_REMOVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/agendamento/*/status").hasAuthority("AGENDAMENTO_ALTERAR_STATUS")
                        .requestMatchers(HttpMethod.GET, "/api/v1/evolucao/**").hasAuthority("EVOLUCAO_VISUALIZAR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/evolucao/**").hasAuthority("EVOLUCAO_EDITAR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/evolucao/**").hasAuthority("EVOLUCAO_REMOVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pagamentos/**").hasAuthority("PAGAMENTO_ALTERAR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tipo-atendimento/**").hasAuthority("TIPO_ATENDIMENTO_VISUALIZAR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tipo-atendimento").hasAuthority("TIPO_ATENDIMENTO_CRIAR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tipo-atendimento/**").hasAuthority("TIPO_ATENDIMENTO_EDITAR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tipo-atendimento/**").hasAuthority("TIPO_ATENDIMENTO_REMOVER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/autenticacao/usuarios/**").hasAuthority("PAPEL_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = securityProperties.cors().allowedOrigins().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(this::removeTrailingSlash)
                .toList();

        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        if (!allowedOrigins.isEmpty()) {
            configuration.setAllowedOriginPatterns(allowedOrigins);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private String removeTrailingSlash(String origin) {
        if (origin.length() > 1 && origin.endsWith("/")) {
            return origin.substring(0, origin.length() - 1);
        }

        return origin;
    }
}

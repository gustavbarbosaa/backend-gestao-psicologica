package br.com.gestaopsicologica;

import br.com.gestaopsicologica.config.AdminBootstrapProperties;
import br.com.gestaopsicologica.config.SecurityProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperties.class, AdminBootstrapProperties.class})
public class GestaoPsicologicaApplication {

	@Value("${app.timezone:America/Fortaleza}")
	private String applicationTimeZone;

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZone));
	}

	public static void main(String[] args) {
		SpringApplication.run(GestaoPsicologicaApplication.class, args);
	}

}

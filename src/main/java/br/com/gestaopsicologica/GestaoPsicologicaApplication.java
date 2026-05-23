package br.com.gestaopsicologica;

import br.com.gestaopsicologica.config.SecurityProperties;
import br.com.gestaopsicologica.config.AdminBootstrapProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperties.class, AdminBootstrapProperties.class})
public class GestaoPsicologicaApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Ameria/Sao_Paulo"));
	}

	public static void main(String[] args) {
		SpringApplication.run(GestaoPsicologicaApplication.class, args);
	}

}

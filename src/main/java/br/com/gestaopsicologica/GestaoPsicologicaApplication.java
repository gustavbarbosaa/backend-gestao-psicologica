package br.com.gestaopsicologica;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GestaoPsicologicaApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Ameria/Sao_Paulo"));
	}

	public static void main(String[] args) {
		SpringApplication.run(GestaoPsicologicaApplication.class, args);
	}

}

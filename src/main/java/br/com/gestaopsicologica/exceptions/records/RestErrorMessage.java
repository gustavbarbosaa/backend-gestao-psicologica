package br.com.gestaopsicologica.exceptions.records;

import org.springframework.http.HttpStatus;

import java.util.List;

public record RestErrorMessage(HttpStatus status, String message, List<String> erros) {
}

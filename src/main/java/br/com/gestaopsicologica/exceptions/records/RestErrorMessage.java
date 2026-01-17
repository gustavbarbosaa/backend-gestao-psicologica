package br.com.gestaopsicologica.exceptions.records;

import org.springframework.http.HttpStatus;

public record RestErrorMessage(HttpStatus status, String message) {
}

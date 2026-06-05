package br.com.gestaopsicologica.exceptions;

import br.com.gestaopsicologica.exceptions.records.RestErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestErrorMessage> handleAuthenticationException(AuthenticationException e){

        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.UNAUTHORIZED,
                "Falha ao autenticar",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestErrorMessage> handleIllegalArgumentException(IllegalArgumentException e){

        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.CONFLICT,
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleEntityNotFound(EntityNotFoundException e) {

        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PagamentoPendenteException.class)
    public ResponseEntity<RestErrorMessage> pagamentoPendente(PagamentoPendenteException e) {
        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(StatusAtendimentoInvalidoException.class)
    public ResponseEntity<RestErrorMessage> statusAtendimentoInvalido(StatusAtendimentoInvalidoException e) {
        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorMessage> handleValidationException(MethodArgumentNotValidException e) {

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        RestErrorMessage response = new RestErrorMessage(
                HttpStatus.CONFLICT,
                "Erro de validação",
                errors
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestErrorMessage> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        String message = e.getReason() != null ? e.getReason() : "Erro na requisição";

        RestErrorMessage response = new RestErrorMessage(
                status,
                message,
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }
}

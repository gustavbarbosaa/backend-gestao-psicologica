package br.com.gestaopsicologica.controllers;

import br.com.gestaopsicologica.DTO.requests.AlterarStatusPagamentoRequest;
import br.com.gestaopsicologica.DTO.responses.AgendamentoResponse;
import br.com.gestaopsicologica.services.agendamento.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
    private final AgendamentoService agendamentoService;

    @PatchMapping("/{agendamentoId}/pagamento")
    public ResponseEntity<AgendamentoResponse> alterarStatusPagamento(
            @PathVariable UUID agendamentoId,
            @Valid @RequestBody AlterarStatusPagamentoRequest request
    ) {
        AgendamentoResponse response = agendamentoService.alterarStatusPagamentoAtendimento(
                agendamentoId,
                request.statusPagamento()
        );

        return ResponseEntity.ok(response);
    }
}

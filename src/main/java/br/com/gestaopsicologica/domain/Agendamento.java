package br.com.gestaopsicologica.domain;

import br.com.gestaopsicologica.enums.StatusAtendimento;
import br.com.gestaopsicologica.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento implements Serializable {
    @Serial
    private static final long serialVersionUID = -6264017513353050754L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private Integer duracaoEmMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Column(nullable = false)
    private BigDecimal valorAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento")
    private StatusPagamento statusPagamento;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento statusAtendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_atendimento_id")
    private TipoAtendimento tipoAtendimento;

    public LocalDateTime getDataHoraFim() {
        return this.dataHoraInicio.plusMinutes(this.duracaoEmMinutos);
    }
}

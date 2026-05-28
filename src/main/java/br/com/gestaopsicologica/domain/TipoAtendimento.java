package br.com.gestaopsicologica.domain;

import br.com.gestaopsicologica.enums.TipoAtendimentoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class TipoAtendimento implements Serializable {
    @Serial
    private static final long serialVersionUID = 6336991295187594333L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TipoAtendimentoEnum nome;

    @Column(nullable = false)
    private BigDecimal valorPadraoTipoAtendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Boolean ativo;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime alteradoEm;
}

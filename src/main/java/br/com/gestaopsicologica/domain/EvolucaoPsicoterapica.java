package br.com.gestaopsicologica.domain;

import br.com.gestaopsicologica.security.criptografia.EvolucaoCriptografiaConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolucaoPsicoterapica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id")
    private Agendamento agendamento;

    @Convert(converter = EvolucaoCriptografiaConverter.class)
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Convert(converter = EvolucaoCriptografiaConverter.class)
    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean ativo = true;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime dataAlteracao;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime dataCriacao;
}

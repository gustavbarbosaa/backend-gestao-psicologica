package br.com.gestaopsicologica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "paciente")
public class Paciente implements Serializable {

    @Serial
    private static final long serialVersionUID = -6381254400152342206L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    @Email(message = "E-mail no formato inválido")
    private String email;

    @Column(nullable = false)
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profissional_vinculado", nullable = false, referencedColumnName = "id")
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean ativo = true;

    @Column
    @CreationTimestamp
    private LocalDateTime dataAlteracao;

    @Column
    @UpdateTimestamp
    private LocalDateTime dataCriacao;
}

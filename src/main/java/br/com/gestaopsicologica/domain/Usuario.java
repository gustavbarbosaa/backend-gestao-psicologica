package br.com.gestaopsicologica.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Email(message = "E-mail no formato inválido")
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;
}

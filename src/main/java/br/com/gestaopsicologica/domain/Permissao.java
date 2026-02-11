package br.com.gestaopsicologica.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
public class Permissao implements Serializable {

    @Serial
    private static final long serialVersionUID = -7132946004259505318L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String codigo;
}

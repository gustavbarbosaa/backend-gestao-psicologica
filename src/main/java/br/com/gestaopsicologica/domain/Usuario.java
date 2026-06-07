package br.com.gestaopsicologica.domain;

import br.com.gestaopsicologica.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "usuario")
public class Usuario implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 5506623895855407795L;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "papeis_usuario",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    @Builder.Default
    private Set<Papel> papeis =  new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column
    @UpdateTimestamp
    private LocalDateTime dataAlteracao;

    @Column
    @CreationTimestamp
    private LocalDateTime dataCriacao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Papel papel : papeis) {
            authorities.add(new SimpleGrantedAuthority("PAPEL_" + papel.getNome()));

            for (Permissao permissao : papel.getPermissoes()) {
                authorities.add(new SimpleGrantedAuthority(permissao.getCodigo()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

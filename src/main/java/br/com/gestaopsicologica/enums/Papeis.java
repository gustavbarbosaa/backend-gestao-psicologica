package br.com.gestaopsicologica.enums;

import lombok.Getter;

@Getter
public enum Papeis {
    ADMIN("ADMIN"),
    PROFISSIONAL("PROFISSIONAL");

    private final String descricao;

    Papeis(String descricao) {
        this.descricao = descricao;
    }

}

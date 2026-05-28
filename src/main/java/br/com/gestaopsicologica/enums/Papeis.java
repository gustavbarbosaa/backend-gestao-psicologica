package br.com.gestaopsicologica.enums;

public enum Papeis {
    ADMIN("ADMIN"),
    PROFISSIONAL("PROFISSIONAL");

    private final String descricao;

    Papeis(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}

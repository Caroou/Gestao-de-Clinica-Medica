package com.clinicamedica.main.model;

import jakarta.persistence.*; // Importa as anotações do JPA

@Entity // Diz ao Spring: "Isto mapeia uma tabela na base de dados!"
@Table(name = "usuario") // Opcional, mas garante que o nome da tabela é exato
public class Usuario {

    @Id // Define que este campo é a Chave Primária (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O equivalente ao AUTO_INCREMENT
    private int id;

    @Column(unique = true, nullable = false) // Diz que a coluna é do tipo unique e not null
    private String login;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // Diz ao Spring para guardar o Enum como texto (ex: "ADMIN") e não como um número/índice
    @Column(nullable = false, name = "perfil") // O atributo name é utilizado caso o nome da coluna seja diferente da variavel
    private PerfilUsuario perfilUsuario;

    private boolean ativo = true;

    public Usuario(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public PerfilUsuario getPerfilUsuario() {
        return perfilUsuario;
    }

    public void setPerfilUsuario(PerfilUsuario perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    // Para variáveis booleanas (verdadeiro/falso), o padrão do Java é usar "is" em vez de "get"
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
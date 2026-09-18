package com.clinicamedica.model;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String perfil;
    private boolean autorizado;
    
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

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    // Para variáveis booleanas (verdadeiro/falso), o padrão do Java é usar "is" em vez de "get"
    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }
}
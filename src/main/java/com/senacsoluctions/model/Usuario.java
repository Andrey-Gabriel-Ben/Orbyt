package com.senacsoluctions.model;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String login;
    private String senha;
    private String cargo; // Pode ser "ATENDENTE" ou "TECNICO"

    public Usuario() {}

    public Usuario(int idUsuario, String nome, String login, String senha, String cargo) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.cargo = cargo;
    }

    // Getters e Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}
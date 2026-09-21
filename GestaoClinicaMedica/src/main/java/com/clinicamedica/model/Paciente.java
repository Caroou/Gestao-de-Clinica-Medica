package com.clinicamedica.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private int id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private List<Consulta> consultas;

    public Paciente() {
        this.consultas = new ArrayList<>();
    }

    public Paciente(int id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.consultas = new ArrayList<>();
    }

    public void cadastrar(){
        System.out.println("Paciente cadastrado com sucesso!");
    }

    public void consultarHistorico(){
        System.out.println("Consultando histórico do paciente...");
        if (consultas == null || consultas.isEmpty()) {
            System.out.println("Nenhuma consulta registrada para este paciente.");
        } else {
            for (Consulta c : consultas) {
                System.out.println("Consulta ID: " + c.getId() + " - Data: " + c.getDataHora() + " - Status: " + c.getStatus());
            }
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
package com.clinicamedica.model;

import java.util.ArrayList;
import java.util.List;

public class Medico {
    private int id;
    private String nome;
    private String crm;
    private String telefone;
    private List<Especialidade> especialidades;
    private Agenda agenda;

    public Medico(){
        this.especialidades = new ArrayList<>();
        this.agenda = new Agenda();
    }

    public Medico(int id, String nome, String crm, String telefone){
        this.id = id;
        this.nome = nome;
        this.crm = crm;
        this.telefone = telefone;
        this.especialidades = new ArrayList<>();
        this.agenda = new Agenda();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidade> especialidades) {
        this.especialidades = especialidades;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public void cadastrar() {
        System.out.println("Médico cadastrado com sucesso.");
    }

    public void consultarAgenda() {
        if (this.agenda != null) {
            System.out.println("Consultando agenda do médico: " + this.nome);
        }
    }
}
package com.clinicamedica.main.model;

import java.util.ArrayList;
import java.util.List;

import com.clinicamedica.main.config.CriptografiaAesConverter;
import jakarta.persistence.*;
@Entity
@Table(name = "medico")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario; // Vínculo com o login

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String crm;

    @Column(nullable = false)
    @Convert(converter = CriptografiaAesConverter.class)
    private String telefone;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "medico_especialidade",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidade_id")
    )
    private List<Especialidade> especialidades;

    @OneToMany(mappedBy = "medico")
    private List<Agenda> agendas; // Agora é uma lista de slots de horário

    public Medico() {
        this.especialidades = new ArrayList<>();
        this.agendas = new ArrayList<>();
    }

    public Medico(int id, Usuario usuario, String nome, String crm, String telefone) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.crm = crm;
        this.telefone = telefone;
        this.especialidades = new ArrayList<>();
        this.agendas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public List<Agenda> getAgendas() {
        return agendas;
    }

    public void setAgendas(List<Agenda> agendas) {
        this.agendas = agendas;
    }

}
package com.clinicamedica.main.model;

import com.clinicamedica.main.config.CriptografiaAesConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    //Para chaves estrangeiras, usamos @JoinColumn em vez de @Column
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario; // Vínculo com o login

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    @Convert(converter = CriptografiaAesConverter.class)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    @Convert(converter = CriptografiaAesConverter.class)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas;

    public Paciente() {
        this.consultas = new ArrayList<>();
    }

    public Paciente(int id, Usuario usuario, String nome, String cpf, LocalDate dataNascimento, String telefone,
            String email) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.consultas = new ArrayList<>();
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

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
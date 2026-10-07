package com.clinicamedica.main.model;

import jakarta.persistence.*;
import com.clinicamedica.main.config.StatusConsultaConverter;
@Entity
@Table(name = "consulta")
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda; // Já carrega o médico, o dia e a hora

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "especialidade_id")
    private Especialidade especialidade;

    @Convert(converter = StatusConsultaConverter.class)
    @Column(nullable = false)
    private StatusConsulta status;

    @Column(nullable = true)
    private String observacoes;

    public Consulta() {
    }

    public Consulta(int id, Agenda agenda, Paciente paciente, StatusConsulta status, String observacoes) {
        this.id = id;
        this.agenda = agenda;
        this.paciente = paciente;
        this.status = status;
        this.observacoes = observacoes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

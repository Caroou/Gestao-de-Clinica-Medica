package com.clinicamedica.model;

import java.time.LocalDateTime;

public class Consulta {

    private int id;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String observacoes;
    private Paciente paciente;
    private Medico medico;
    private Agenda agenda;

    public Consulta() {
    }

    public Consulta(int id, LocalDateTime dataHora, StatusConsulta status, String observacoes, Paciente paciente, Medico medico, Agenda agenda) {
        this.id = id;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
        this.paciente = paciente;
        this.medico = medico;
        this.agenda = agenda;
    }

    public void agendar() {
        this.status = StatusConsulta.AGENDADO;
    }

    public void cancelar() {
        this.status = StatusConsulta.CANCELADO;
    }

    public void reagendar(LocalDateTime novaDataHora) {
        this.dataHora = novaDataHora;
        this.status = StatusConsulta.AGENDADO;
    }

    public void realizar() {
        this.status = StatusConsulta.REALIZADO;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }
}
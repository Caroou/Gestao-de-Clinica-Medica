package com.clinicamedica.gestaoclinicamedica.model;
import java.time.LocalDateTime;

public class Consulta {

    private int id;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String observacoes;

    public Consulta() {
    }

    public Consulta(int id, LocalDateTime dataHora, StatusConsulta status, String observacoes) {
        this.id = id;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
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

    // Getters e Setters para permitir que os DAOs e Controllers acedam aos dados
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
}
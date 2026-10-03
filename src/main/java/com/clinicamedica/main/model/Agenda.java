package com.clinicamedica.main.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
@Entity
@Table(name = "agenda")
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(name = "data_agenda", nullable = false)
    private LocalDate dataAgenda;

    @Column(nullable = false)
    private LocalTime horario; // O slot de tempo específico

    private boolean disponivel;

    public Agenda() {
        this.disponivel = true;
    }

    public Agenda(int id, Medico medico, LocalDate dataAgenda, LocalTime horario, boolean disponivel) {
        this.id = id;
        this.medico = medico;
        this.dataAgenda = dataAgenda;
        this.horario = horario;
        this.disponivel = disponivel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDate getDataAgenda() {
        return dataAgenda;
    }

    public void setDataAgenda(LocalDate dataAgenda) {
        this.dataAgenda = dataAgenda;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
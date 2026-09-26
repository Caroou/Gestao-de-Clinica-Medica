package com.clinicamedica.main.model;

import java.time.LocalDateTime;

import com.clinicamedica.main.config.CriptografiaAesConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "atendimento")
public class Atendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "data_registro")
    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = CriptografiaAesConverter.class)
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = CriptografiaAesConverter.class)
    private String prescricao;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = CriptografiaAesConverter.class)
    private String observacoes;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    public Atendimento(int id, LocalDateTime dataHora, String diagnostico, String prescricao, String observacoes, Consulta consulta) {
        this.id = id;
        this.dataHora = dataHora;
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.observacoes = observacoes;
        this.consulta = consulta;
    }

    public Atendimento() {
    }

    public void registrar() {
        // Lógica para registrar o atendimento
        System.out.println("Atendimento registrado com sucesso.");
    }

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

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}

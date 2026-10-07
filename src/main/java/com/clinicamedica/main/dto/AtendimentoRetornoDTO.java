package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.Atendimento;

import java.time.LocalDateTime;

public record AtendimentoRetornoDTO(
        Integer id,
        Integer consultaId,
        String nomePaciente,
        LocalDateTime dataHora,
        String diagnostico,
        String prescricao,
        String observacoes
) {
    public AtendimentoRetornoDTO(Atendimento atendimento) {
        this(
                atendimento.getId(),
                atendimento.getConsulta().getId(),
                atendimento.getConsulta().getPaciente().getNome(),
                atendimento.getDataHora(),
                atendimento.getDiagnostico(),
                atendimento.getPrescricao(),
                atendimento.getObservacoes()
        );
    }
}

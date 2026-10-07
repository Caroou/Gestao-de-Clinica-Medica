package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.Consulta;
import com.clinicamedica.main.model.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaRetornoDTO(
        Integer id,
        Integer pacienteId,
        String nomePaciente,
        Integer medicoId,
        String nomeMedico,
        Integer especialidadeId,
        String nomeEspecialidade,
        Integer agendaId,
        LocalDateTime dataHora,
        StatusConsulta status,
        String observacoes
) {
    public ConsultaRetornoDTO(Consulta consulta) {
        this(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getPaciente().getNome(),
                consulta.getAgenda().getMedico().getId(),
                consulta.getAgenda().getMedico().getNome(),
                consulta.getEspecialidade() == null ? null : consulta.getEspecialidade().getId(),
                consulta.getEspecialidade() == null ? "Especialidade não informada" : consulta.getEspecialidade().getNome(),
                consulta.getAgenda().getId(),
                LocalDateTime.of(consulta.getAgenda().getDataAgenda(), consulta.getAgenda().getHorario()),
                consulta.getStatus(),
                consulta.getObservacoes()
        );
    }
}

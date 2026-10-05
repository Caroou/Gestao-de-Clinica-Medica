package com.clinicamedica.dto;

import com.clinicamedica.model.StatusConsulta;
import java.time.LocalDateTime;

public record ConsultaResponseDTO(
    Integer id,
    Integer pacienteId,
    String nomePaciente,
    Integer medicoId,
    String nomeMedico,
    LocalDateTime dataHora,
    StatusConsulta status,
    String observacoes
) {}

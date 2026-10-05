package com.clinicamedica.dto;

import java.time.LocalDateTime;

public record ConsultaRequestDTO(
    Integer pacienteId,
    Integer medicoId,
    LocalDateTime dataHora,
    String observacoes
) {}

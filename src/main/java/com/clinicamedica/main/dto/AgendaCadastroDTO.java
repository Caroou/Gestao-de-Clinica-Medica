package com.clinicamedica.main.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendaCadastroDTO(
        LocalDate dataAgenda,
        LocalTime horario) {
}

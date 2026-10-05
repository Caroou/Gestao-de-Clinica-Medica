package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.Agenda;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendaRetornoDTO(

        Integer id,
        Integer medicoId,
        LocalDate dataAgenda,
        LocalTime horario,
        boolean disponivel
) {

    public AgendaRetornoDTO(Agenda agenda) {
        this(
                agenda.getId(),
                agenda.getMedico().getId(),
                agenda.getDataAgenda(),
                agenda.getHorario(),
                agenda.isDisponivel()
        );
    }
}

package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.Paciente;
import java.time.LocalDate;

public record PacienteRetornoDTO(
        Integer id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        String email
) {
    // Construtor para converter a Entidade neste DTO em 1 linha lá no Service
    public PacienteRetornoDTO(Paciente paciente) {
        this(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(), // O AES já vai ter descriptografado isso automaticamente!
                paciente.getDataNascimento(),
                paciente.getTelefone(), // O AES já vai ter descriptografado isso também!
                paciente.getEmail()
        );
    }
}
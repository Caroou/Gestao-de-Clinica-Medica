package com.clinicamedica.main.dto;

import java.time.LocalDate;

public record PacienteCadastroDTO(
        Integer usuarioId, // O ID do usuário que acabou de ser criado na tela de login
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        String email
) {}
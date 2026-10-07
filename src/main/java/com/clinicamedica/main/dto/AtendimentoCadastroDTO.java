package com.clinicamedica.main.dto;

public record AtendimentoCadastroDTO(
        Integer consultaId,
        String diagnostico,
        String prescricao,
        String observacoes
) {
}

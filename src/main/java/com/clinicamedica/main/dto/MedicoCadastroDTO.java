package com.clinicamedica.main.dto;

import java.util.List;

public record MedicoCadastroDTO(

        Integer usuarioId,
        String nome,
        String crm,
        String telefone,
        List<Integer> especialidadeIds

) {}
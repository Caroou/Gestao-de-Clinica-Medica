package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.Medico;

import java.util.List;

public record MedicoRetornoDTO(

        Integer id,
        Integer usuarioId,
        String nome,
        String crm,
        String telefone,
        List<Integer> especialidadeIds

) {

    public MedicoRetornoDTO(Medico medico) {

        this(
                medico.getId(),
                medico.getUsuario().getId(),
                medico.getNome(),
                medico.getCrm(),
                medico.getTelefone(),
                medico.getEspecialidades()
                        .stream()
                        .map(especialidade -> especialidade.getId())
                        .toList()
        );
    }
}
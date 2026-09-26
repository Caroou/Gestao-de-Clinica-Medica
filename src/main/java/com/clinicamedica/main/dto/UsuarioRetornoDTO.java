package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.PerfilUsuario;
import com.clinicamedica.main.model.Usuario;

public record UsuarioRetornoDTO(Integer id, String login, PerfilUsuario perfilUsuario) {

    // Esse construtor extra ajuda a transformar uma entidade Usuario do banco direto neste DTO
    public UsuarioRetornoDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin(), usuario.getPerfilUsuario());
    }
}
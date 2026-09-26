package com.clinicamedica.main.dto;

import com.clinicamedica.main.model.PerfilUsuario;

public record UsuarioCadastroDTO(String login, String senha, PerfilUsuario perfilUsuario) {
}
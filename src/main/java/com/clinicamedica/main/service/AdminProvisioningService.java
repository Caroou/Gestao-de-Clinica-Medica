package com.clinicamedica.main.service;

import com.clinicamedica.main.model.PerfilUsuario;
import com.clinicamedica.main.model.Usuario;
import com.clinicamedica.main.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class AdminProvisioningService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminProvisioningService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void criarAdministrador(String login, String senha) {
        String loginNormalizado = login == null ? "" : login.trim();
        int tamanhoSenha = senha == null ? 0 : senha.getBytes(StandardCharsets.UTF_8).length;

        if (loginNormalizado.isBlank() || loginNormalizado.length() > 100) {
            throw new IllegalArgumentException("O login é obrigatório e deve ter no máximo 100 caracteres.");
        }
        if (senha == null || senha.isBlank() || tamanhoSenha < 15 || tamanhoSenha > 72) {
            throw new IllegalArgumentException("A senha deve ter entre 15 e 72 bytes UTF-8.");
        }
        if (usuarioRepository.findByLogin(loginNormalizado).isPresent()) {
            throw new IllegalArgumentException("Este login já está cadastrado. Nenhuma conta foi alterada.");
        }

        Usuario admin = new Usuario();
        admin.setLogin(loginNormalizado);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setPerfilUsuario(PerfilUsuario.ADMIN);
        admin.setAtivo(true);
        usuarioRepository.save(admin);
    }
}

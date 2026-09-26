package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.UsuarioCadastroDTO;
import com.clinicamedica.main.dto.UsuarioRetornoDTO;
import com.clinicamedica.main.model.Usuario;
import com.clinicamedica.main.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioRetornoDTO cadastrarUsuario(UsuarioCadastroDTO dados) {
        // 1. Transforma o DTO que chegou em uma Entidade real
        Usuario usuario = new Usuario();
        usuario.setLogin(dados.login());
        usuario.setPerfilUsuario(dados.perfilUsuario());
        usuario.setAtivo(true);

        // 2. Criptografa a senha
        BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
        usuario.setSenha(codificador.encode(dados.senha()));

        // 3. Salva no banco
        repository.save(usuario);

        // 4. Devolve o DTO de retorno (sem a senha)
        return new UsuarioRetornoDTO(usuario);
    }

    public UsuarioRetornoDTO fazerLogin(String login, String senhaDigitada) {
        Usuario usuario = repository.findByLoginAndAtivoTrue(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado ou inativo."));

        BCryptPasswordEncoder verificador = new BCryptPasswordEncoder();

        if (verificador.matches(senhaDigitada, usuario.getSenha())) {
            // Sucesso! Devolve os dados blindados
            return new UsuarioRetornoDTO(usuario);
        } else {
            throw new IllegalArgumentException("Senha incorreta.");
        }
    }
}
package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.UsuarioCadastroDTO;
import com.clinicamedica.main.dto.UsuarioRetornoDTO;
import com.clinicamedica.main.model.PerfilUsuario;
import com.clinicamedica.main.model.Usuario;
import com.clinicamedica.main.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.clinicamedica.main.validation.ValidacaoDados;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioRetornoDTO cadastrarUsuario(UsuarioCadastroDTO dados) {
        if (dados == null) throw new IllegalArgumentException("Dados da conta são obrigatórios.");
        if (dados.perfilUsuario() != PerfilUsuario.PACIENTE
                && dados.perfilUsuario() != PerfilUsuario.MEDICO) {
            throw new IllegalArgumentException("Este cadastro aceita apenas os perfis Paciente e Médico.");
        }
        String login = ValidacaoDados.textoObrigatorio(dados.login(), "o login", 100);
        if (repository.existsByLogin(login)) throw new IllegalArgumentException("Este login já está em uso.");
        if (dados.senha() == null || dados.senha().length() < 8 || ValidacaoDados.bytesUtf8(dados.senha()) > 72) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres e até 72 bytes.");
        }

        // 1. Transforma o DTO que chegou em uma Entidade real
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setPerfilUsuario(dados.perfilUsuario());
        usuario.setAtivo(true);

        // 2. Criptografa a senha
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        // 3. Salva no banco
        repository.save(usuario);

        // 4. Devolve o DTO de retorno (sem a senha)
        return new UsuarioRetornoDTO(usuario);
    }

    public UsuarioRetornoDTO fazerLogin(String login, String senhaDigitada) {
        if (login == null || login.isBlank() || senhaDigitada == null || senhaDigitada.isEmpty()) {
            throw new IllegalArgumentException("Informe login e senha.");
        }
        Usuario usuario = repository.findByLoginAndAtivoTrue(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado ou inativo."));

        if (passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
            // Sucesso! Devolve os dados blindados
            return new UsuarioRetornoDTO(usuario);
        } else {
            throw new IllegalArgumentException("Senha incorreta.");
        }
    }
}

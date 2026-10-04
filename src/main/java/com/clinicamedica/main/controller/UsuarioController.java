package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.UsuarioCadastroDTO;
import com.clinicamedica.main.dto.UsuarioLoginDTO;
import com.clinicamedica.main.dto.UsuarioRetornoDTO;
import com.clinicamedica.main.config.SessionAuthenticationFilter;
import com.clinicamedica.main.model.PerfilUsuario;
import com.clinicamedica.main.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioCadastroDTO dados) {
        try {
            UsuarioRetornoDTO usuarioCriado = usuarioService.cadastrarUsuario(dados);
            return ResponseEntity.ok(usuarioCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO dados, HttpServletRequest request) {
        try {
            UsuarioRetornoDTO usuarioLogado = usuarioService.fazerLogin(dados.login(), dados.senha());
            HttpSession sessaoAnterior = request.getSession(false);
            if (sessaoAnterior != null) {
                sessaoAnterior.invalidate();
            }
            HttpSession sessao = request.getSession(true);
            sessao.setAttribute(SessionAuthenticationFilter.USER_ID, usuarioLogado.id());
            sessao.setAttribute(SessionAuthenticationFilter.USER_LOGIN, usuarioLogado.login());
            sessao.setAttribute(SessionAuthenticationFilter.USER_PROFILE, usuarioLogado.perfilUsuario());
            return ResponseEntity.ok(usuarioLogado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sessao")
    public ResponseEntity<UsuarioRetornoDTO> sessao(HttpSession sessao) {
        return ResponseEntity.ok(new UsuarioRetornoDTO(
                (Integer) sessao.getAttribute(SessionAuthenticationFilter.USER_ID),
                (String) sessao.getAttribute(SessionAuthenticationFilter.USER_LOGIN),
                (PerfilUsuario) sessao.getAttribute(SessionAuthenticationFilter.USER_PROFILE)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession sessao) {
        sessao.invalidate();
        return ResponseEntity.noContent().build();
    }
}

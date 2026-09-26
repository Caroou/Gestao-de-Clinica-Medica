package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.UsuarioCadastroDTO;
import com.clinicamedica.main.dto.UsuarioLoginDTO;
import com.clinicamedica.main.dto.UsuarioRetornoDTO;
import com.clinicamedica.main.service.UsuarioService;
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
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO dados) {
        try {
            UsuarioRetornoDTO usuarioLogado = usuarioService.fazerLogin(dados.login(), dados.senha());
            return ResponseEntity.ok(usuarioLogado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
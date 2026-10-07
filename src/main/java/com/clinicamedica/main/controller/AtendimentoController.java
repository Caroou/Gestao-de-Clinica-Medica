package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.AtendimentoCadastroDTO;
import com.clinicamedica.main.dto.AtendimentoRetornoDTO;
import com.clinicamedica.main.service.AtendimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping
    public ResponseEntity<AtendimentoRetornoDTO> registrar(@RequestBody AtendimentoCadastroDTO dados,
            Authentication autenticacao) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.registrar(dados, autenticacao.getName()));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<AtendimentoRetornoDTO>> listarDoPaciente(Authentication autenticacao) {
        return ResponseEntity.ok(atendimentoService.listarDoPaciente(autenticacao.getName()));
    }

    @GetMapping("/medico/meus")
    public ResponseEntity<List<AtendimentoRetornoDTO>> listarDoMedico(Authentication autenticacao) {
        return ResponseEntity.ok(atendimentoService.listarDoMedico(autenticacao.getName()));
    }
}

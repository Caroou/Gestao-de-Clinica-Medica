package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.AgendaCadastroDTO;
import com.clinicamedica.main.dto.AgendaRetornoDTO;
import com.clinicamedica.main.service.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    public ResponseEntity<AgendaRetornoDTO> cadastrar(@RequestBody AgendaCadastroDTO dados, Authentication autenticacao) {
        return ResponseEntity.ok(agendaService.cadastrar(dados, autenticacao.getName()));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<AgendaRetornoDTO>> listarDisponiveis(@RequestParam(required = false) Integer medicoId) {
        return ResponseEntity.ok(agendaService.listarDisponiveis(medicoId));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<AgendaRetornoDTO>> listarMinhaAgenda(Authentication autenticacao) {
        return ResponseEntity.ok(agendaService.listarDoMedico(autenticacao.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaRetornoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(agendaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id, Authentication autenticacao) {
        agendaService.excluir(id, autenticacao.getName());
        return ResponseEntity.noContent().build();
    }
}

package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.AgendaCadastroDTO;
import com.clinicamedica.main.dto.AgendaRetornoDTO;
import com.clinicamedica.main.service.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    public ResponseEntity<AgendaRetornoDTO> cadastrar(@RequestBody AgendaCadastroDTO dados) {
        return ResponseEntity.ok(agendaService.cadastrar(dados));
    }

    @GetMapping
    public ResponseEntity<List<AgendaRetornoDTO>> listarTodos() {
        return ResponseEntity.ok(agendaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaRetornoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(agendaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        agendaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.PacienteCadastroDTO;
import com.clinicamedica.main.dto.PacienteRetornoDTO;
import com.clinicamedica.main.dto.PacienteAdminAtualizacaoDTO;
import com.clinicamedica.main.config.SessionAuthenticationFilter;
import jakarta.servlet.http.HttpSession;
import com.clinicamedica.main.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Rota para cadastrar um novo paciente
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody PacienteCadastroDTO dados, HttpSession sessao) {
        try {
            Integer usuarioId = (Integer) sessao.getAttribute(SessionAuthenticationFilter.USER_ID);
            PacienteRetornoDTO novoPaciente = pacienteService.cadastrarPaciente(dados, usuarioId);
            return ResponseEntity.ok(novoPaciente);
        } catch (IllegalArgumentException e) {
            // Se o Service gritar que o CPF ou Email já existe, cai aqui no erro 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Rota para listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<PacienteRetornoDTO>> listarTodos() {
        List<PacienteRetornoDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDadosAdministrativos(
            @PathVariable Integer id,
            @RequestBody PacienteAdminAtualizacaoDTO dados) {
        try {
            return ResponseEntity.ok(pacienteService.atualizarDadosAdministrativos(id, dados));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

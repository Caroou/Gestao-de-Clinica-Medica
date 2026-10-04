package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.MedicoCadastroDTO;
import com.clinicamedica.main.dto.MedicoRetornoDTO;
import com.clinicamedica.main.dto.MedicoAdminAtualizacaoDTO;
import com.clinicamedica.main.config.SessionAuthenticationFilter;
import jakarta.servlet.http.HttpSession;
import com.clinicamedica.main.service.MedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody MedicoCadastroDTO dados,
            HttpSession sessao) {

        try {

            MedicoRetornoDTO medico =
                    medicoService.cadastrarMedico(dados,
                            (Integer) sessao.getAttribute(SessionAuthenticationFilter.USER_ID));

            return ResponseEntity.ok(medico);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicoRetornoDTO>> listarTodos() {

        return ResponseEntity.ok(
                medicoService.listarTodos()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDadosAdministrativos(
            @PathVariable Integer id,
            @RequestBody MedicoAdminAtualizacaoDTO dados) {
        try {
            return ResponseEntity.ok(medicoService.atualizarDadosAdministrativos(id, dados));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoRetornoDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                medicoService.buscarPorId(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Integer id) {

        medicoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}

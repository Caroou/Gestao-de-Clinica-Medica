package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.MedicoCadastroDTO;
import com.clinicamedica.main.dto.MedicoRetornoDTO;
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
            @RequestBody MedicoCadastroDTO dados) {

        try {

            MedicoRetornoDTO medico =
                    medicoService.cadastrarMedico(dados);

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

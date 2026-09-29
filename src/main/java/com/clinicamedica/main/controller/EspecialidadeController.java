package com.clinica.medica.controller;

import com.clinica.medica.dto.EspecialidadeRequestDTO;
import com.clinica.medica.dto.EspecialidadeResponseDTO;
import com.clinica.medica.service.EspecialidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadeController {

    private final EspecialidadeService service;

    public EspecialidadeController(EspecialidadeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponseDTO> cadastrar(
            @RequestBody EspecialidadeRequestDTO dto
    ) {

        return ResponseEntity.ok(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> listarTodos() {

        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorId(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody EspecialidadeRequestDTO dto
    ) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Integer id
    ) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
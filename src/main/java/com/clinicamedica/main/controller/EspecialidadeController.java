package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.EspecialidadeRetornoDTO;
import com.clinicamedica.main.dto.EspecialidadeCadastroDTO;
import com.clinicamedica.main.service.EspecialidadeService;
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
    public ResponseEntity<EspecialidadeRetornoDTO> cadastrar(
            @RequestBody EspecialidadeCadastroDTO dto
    ) {

        return ResponseEntity.ok(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeRetornoDTO>> listarTodos() {

        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeRetornoDTO> buscarPorId(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeRetornoDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody EspecialidadeRetornoDTO dto
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
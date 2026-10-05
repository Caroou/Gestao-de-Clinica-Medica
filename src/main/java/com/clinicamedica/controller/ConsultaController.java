package com.clinicamedica.controller;

import com.clinicamedica.dto.ConsultaRequestDTO;
import com.clinicamedica.dto.ConsultaResponseDTO;
import com.clinicamedica.dto.ReagendamentoRequestDTO;
import com.clinicamedica.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> agendar(@RequestBody ConsultaRequestDTO dto) {
        ConsultaResponseDTO criada = consultaService.agendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(consultaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(consultaService.listarPorData(data));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorMedico(@PathVariable Integer medicoId) {
        return ResponseEntity.ok(consultaService.listarPorMedico(medicoId));
    }

    @PutMapping("/{id}/reagendar")
    public ResponseEntity<ConsultaResponseDTO> reagendar(@PathVariable Integer id, @RequestBody ReagendamentoRequestDTO dto) {
        return ResponseEntity.ok(consultaService.reagendar(id, dto));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponseDTO> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(consultaService.cancelar(id));
    }

    @PutMapping("/{id}/realizar")
    public ResponseEntity<ConsultaResponseDTO> realizar(@PathVariable Integer id) {
        return ResponseEntity.ok(consultaService.realizar(id));
    }
}
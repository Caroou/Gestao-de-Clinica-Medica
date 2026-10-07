package com.clinicamedica.main.controller;

import com.clinicamedica.main.dto.ConsultaCadastroDTO;
import com.clinicamedica.main.dto.ConsultaRetornoDTO;
import com.clinicamedica.main.dto.ConsultaReagendamentoDTO;
import com.clinicamedica.main.service.ConsultaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaRetornoDTO> agendar(@RequestBody ConsultaCadastroDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.agendar(dto, auth.getName()));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ConsultaRetornoDTO>> listarDoPaciente(Authentication auth) {
        return ResponseEntity.ok(consultaService.listarDoPaciente(auth.getName()));
    }

    @GetMapping("/medico/minhas")
    public ResponseEntity<List<ConsultaRetornoDTO>> listarDoMedico(Authentication auth) {
        return ResponseEntity.ok(consultaService.listarDoMedico(auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ConsultaRetornoDTO>> listarTodas() {
        return ResponseEntity.ok(consultaService.listarTodas());
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<List<ConsultaRetornoDTO>> listarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(consultaService.listarPorData(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaRetornoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @PutMapping("/{id}/reagendar")
    public ResponseEntity<ConsultaRetornoDTO> reagendar(@PathVariable Integer id,
            @RequestBody ConsultaReagendamentoDTO dto, Authentication auth) {
        return ResponseEntity.ok(consultaService.reagendar(id, dto, auth.getName()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaRetornoDTO> cancelar(@PathVariable Integer id, Authentication auth) {
        return ResponseEntity.ok(consultaService.cancelar(id, auth.getName()));
    }

    @PutMapping("/{id}/cancelar-medico")
    public ResponseEntity<ConsultaRetornoDTO> cancelarPeloMedico(@PathVariable Integer id, Authentication auth) {
        return ResponseEntity.ok(consultaService.cancelarPeloMedico(id, auth.getName()));
    }

    @PutMapping("/{id}/nao-compareceu")
    public ResponseEntity<ConsultaRetornoDTO> marcarNaoCompareceu(@PathVariable Integer id, Authentication auth) {
        return ResponseEntity.ok(consultaService.marcarNaoCompareceu(id, auth.getName()));
    }

}

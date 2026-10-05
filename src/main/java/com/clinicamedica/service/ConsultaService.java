package com.clinicamedica.service;

import com.clinicamedica.dto.ConsultaRequestDTO;
import com.clinicamedica.dto.ConsultaResponseDTO;
import com.clinicamedica.dto.ReagendamentoRequestDTO;
import com.clinicamedica.model.Consulta;
import com.clinicamedica.repository.ConsultaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;

    public ConsultaService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public List<ConsultaResponseDTO> listarTodas() {
        return consultaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public ConsultaResponseDTO agendar(ConsultaRequestDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setPacienteId(dto.pacienteId());
        consulta.setMedicoId(dto.medicoId());
        consulta.setDataHora(dto.dataHora());
        consulta.setObservacoes(dto.observacoes());
        
        Consulta salva = consultaRepository.save(consulta);
        return converterParaDTO(salva);
    }

    public ConsultaResponseDTO reagendar(Long id, ReagendamentoRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com ID: " + id));

        consulta.setDataHora(dto.novaDataHora());
        Consulta atualizada = consultaRepository.save(consulta);
        return converterParaDTO(atualizada);
    }

    public void cancelar(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new RuntimeException("Consulta não encontrada com ID: " + id);
        }
        consultaRepository.deleteById(id);
    }

    private ConsultaResponseDTO converterParaDTO(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getPacienteId(),
                consulta.getMedicoId(),
                consulta.getDataHora(),
                consulta.getObservacoes()
        );
    }
}
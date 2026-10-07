package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.AtendimentoCadastroDTO;
import com.clinicamedica.main.dto.AtendimentoRetornoDTO;
import com.clinicamedica.main.model.Atendimento;
import com.clinicamedica.main.model.Consulta;
import com.clinicamedica.main.model.StatusConsulta;
import com.clinicamedica.main.repository.AtendimentoRepository;
import com.clinicamedica.main.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.clinicamedica.main.validation.ValidacaoDados;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ConsultaRepository consultaRepository;

    public AtendimentoService(AtendimentoRepository atendimentoRepository, ConsultaRepository consultaRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public AtendimentoRetornoDTO registrar(AtendimentoCadastroDTO dados, String loginMedico) {
        if (dados == null) throw new IllegalArgumentException("Informe os dados do atendimento.");
        if (dados.consultaId() == null) throw new IllegalArgumentException("Informe a consulta atendida.");
        if (dados.diagnostico() == null || dados.diagnostico().isBlank()) {
            throw new IllegalArgumentException("O diagnóstico é obrigatório.");
        }
        if (ValidacaoDados.bytesUtf8(dados.diagnostico()) > 48_000
                || ValidacaoDados.bytesUtf8(dados.prescricao()) > 48_000
                || ValidacaoDados.bytesUtf8(dados.observacoes()) > 48_000) {
            throw new IllegalArgumentException("Cada campo clínico deve ter até 48 KB de texto.");
        }

        Consulta consulta = consultaRepository.bloquearPorIdEMedico(dados.consultaId(), loginMedico)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada na agenda deste médico."));
        if (consulta.getStatus() != StatusConsulta.AGENDADO) {
            throw new IllegalArgumentException("Somente uma consulta agendada pode receber atendimento.");
        }
        if (atendimentoRepository.existsByConsulta_Id(consulta.getId())) {
            throw new IllegalArgumentException("Já existe um atendimento registrado para esta consulta.");
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setConsulta(consulta);
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDiagnostico(dados.diagnostico().trim());
        atendimento.setPrescricao(normalizar(dados.prescricao()));
        atendimento.setObservacoes(normalizar(dados.observacoes()));

        atendimento = atendimentoRepository.save(atendimento);
        consulta.setStatus(StatusConsulta.REALIZADO);
        consultaRepository.save(consulta);
        return new AtendimentoRetornoDTO(atendimento);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoRetornoDTO> listarDoPaciente(String loginPaciente) {
        return atendimentoRepository.listarDoPaciente(loginPaciente)
                .stream().map(AtendimentoRetornoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<AtendimentoRetornoDTO> listarDoMedico(String loginMedico) {
        return atendimentoRepository.listarDoMedico(loginMedico)
                .stream().map(AtendimentoRetornoDTO::new).toList();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

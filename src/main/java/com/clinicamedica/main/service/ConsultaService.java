package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.ConsultaCadastroDTO;
import com.clinicamedica.main.dto.ConsultaRetornoDTO;
import com.clinicamedica.main.dto.ConsultaReagendamentoDTO;
import com.clinicamedica.main.model.*;
import com.clinicamedica.main.repository.AgendaRepository;
import com.clinicamedica.main.repository.ConsultaRepository;
import com.clinicamedica.main.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AgendaRepository agendaRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository consultaRepository, AgendaRepository agendaRepository,
                           PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.agendaRepository = agendaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public ConsultaRetornoDTO agendar(ConsultaCadastroDTO dados, String loginPaciente) {
        if (dados == null) throw new IllegalArgumentException("Informe os dados do agendamento.");
        Paciente paciente = pacienteRepository.findByUsuario_Login(loginPaciente)
                .orElseThrow(() -> new IllegalArgumentException("O cadastro do paciente não foi encontrado."));
        Agenda agenda = buscarHorarioDisponivel(dados.agendaId());
        Especialidade especialidade = buscarEspecialidadeDoMedico(dados.especialidadeId(), agenda.getMedico());

        agenda.setDisponivel(false);
        Consulta consulta = new Consulta();
        consulta.setAgenda(agenda);
        consulta.setPaciente(paciente);
        consulta.setEspecialidade(especialidade);
        consulta.setStatus(StatusConsulta.AGENDADO);
        consulta.setObservacoes(normalizar(dados.observacoes()));
        return new ConsultaRetornoDTO(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public List<ConsultaRetornoDTO> listarDoPaciente(String login) {
        return consultaRepository.listarDoPaciente(login)
                .stream().map(ConsultaRetornoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaRetornoDTO> listarDoMedico(String login) {
        return consultaRepository.listarDoMedico(login)
                .stream().map(ConsultaRetornoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaRetornoDTO> listarTodas() {
        return consultaRepository.findAll().stream().map(ConsultaRetornoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaRetornoDTO> listarPorData(LocalDate data) {
        return consultaRepository.findByAgenda_DataAgenda(data).stream().map(ConsultaRetornoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaRetornoDTO buscarPorId(Integer id) {
        return new ConsultaRetornoDTO(buscarConsulta(id));
    }

    @Transactional
    public ConsultaRetornoDTO reagendar(Integer id, ConsultaReagendamentoDTO dados, String loginPaciente) {
        if (dados == null) throw new IllegalArgumentException("Selecione o novo horário.");
        Consulta consulta = consultaRepository.bloquearPorIdEPaciente(id, loginPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada para este paciente."));
        validarAgendada(consulta);
        if (!dataHora(consulta).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível reagendar uma consulta cujo horário já passou.");
        }

        Agenda novaAgenda = buscarHorarioDisponivel(dados.agendaId());
        if (consulta.getAgenda().getId() == novaAgenda.getId()) return new ConsultaRetornoDTO(consulta);

        Agenda agendaAnterior = consulta.getAgenda();
        agendaAnterior.setDisponivel(true);
        novaAgenda.setDisponivel(false);
        consulta.setAgenda(novaAgenda);
        return new ConsultaRetornoDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaRetornoDTO cancelar(Integer id, String loginPaciente) {
        Consulta consulta = consultaRepository.bloquearPorIdEPaciente(id, loginPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada para este paciente."));
        validarAgendada(consulta);
        if (!dataHora(consulta).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível cancelar uma consulta cujo horário já passou.");
        }
        consulta.setStatus(StatusConsulta.CANCELADO);
        consulta.getAgenda().setDisponivel(true);
        return new ConsultaRetornoDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaRetornoDTO cancelarPeloMedico(Integer id, String loginMedico) {
        Consulta consulta = consultaRepository.bloquearPorIdEMedico(id, loginMedico)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada na agenda deste médico."));
        validarAgendada(consulta);
        consulta.setStatus(StatusConsulta.CANCELADO);
        if (dataHora(consulta).isAfter(LocalDateTime.now())) consulta.getAgenda().setDisponivel(true);
        return new ConsultaRetornoDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaRetornoDTO marcarNaoCompareceu(Integer id, String loginMedico) {
        Consulta consulta = consultaRepository.bloquearPorIdEMedico(id, loginMedico)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada na agenda deste médico."));
        validarAgendada(consulta);
        if (dataHora(consulta).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A ausência só pode ser registrada depois do horário da consulta.");
        }
        consulta.setStatus(StatusConsulta.NAO_COMPARECEU);
        return new ConsultaRetornoDTO(consultaRepository.save(consulta));
    }

    private Agenda buscarHorarioDisponivel(Integer agendaId) {
        if (agendaId == null) throw new IllegalArgumentException("Selecione um horário disponível.");
        Agenda agenda = agendaRepository.buscarPorIdParaAtualizacao(agendaId)
                .orElseThrow(() -> new IllegalArgumentException("Horário não encontrado."));
        if (!agenda.isDisponivel()) throw new IllegalArgumentException("Este horário já foi reservado.");
        LocalDateTime dataHora = LocalDateTime.of(agenda.getDataAgenda(), agenda.getHorario());
        if (!dataHora.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Este horário já passou.");
        return agenda;
    }

    private Especialidade buscarEspecialidadeDoMedico(Integer especialidadeId, Medico medico) {
        if (especialidadeId == null) throw new IllegalArgumentException("Selecione a especialidade da consulta.");
        return medico.getEspecialidades().stream()
                .filter(item -> item.getId() == especialidadeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("A especialidade selecionada não pertence a este médico."));
    }

    private Consulta buscarConsulta(Integer id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada."));
    }

    private void validarAgendada(Consulta consulta) {
        if (consulta.getStatus() != StatusConsulta.AGENDADO) {
            throw new IllegalArgumentException("Somente consultas agendadas podem ser alteradas.");
        }
    }

    private LocalDateTime dataHora(Consulta consulta) {
        return LocalDateTime.of(consulta.getAgenda().getDataAgenda(), consulta.getAgenda().getHorario());
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        String normalizado = valor.trim();
        if (normalizado.length() > 1000) throw new IllegalArgumentException("As observações devem ter até 1000 caracteres.");
        return normalizado;
    }
}

package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.AgendaCadastroDTO;
import com.clinicamedica.main.dto.AgendaRetornoDTO;
import com.clinicamedica.main.model.Agenda;
import com.clinicamedica.main.model.Medico;
import com.clinicamedica.main.repository.AgendaRepository;
import com.clinicamedica.main.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final MedicoRepository medicoRepository;

    public AgendaService(
            AgendaRepository agendaRepository,
            MedicoRepository medicoRepository
    ) {
        this.agendaRepository = agendaRepository;
        this.medicoRepository = medicoRepository;
    }

    public AgendaRetornoDTO cadastrar(AgendaCadastroDTO dados) {

        Medico medico = medicoRepository.findById(dados.medicoId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Médico não encontrado.")
                );

        Agenda agenda = new Agenda();

        agenda.setMedico(medico);
        agenda.setDataAgenda(dados.dataAgenda());
        agenda.setHorario(dados.horario());

        Agenda salva = agendaRepository.save(agenda);

        return new AgendaRetornoDTO(salva);
    }

    public List<AgendaRetornoDTO> listarTodos() {

        return agendaRepository.findAll()
                .stream()
                .map(AgendaRetornoDTO::new)
                .toList();
    }

    public AgendaRetornoDTO buscarPorId(Integer id) {

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Agenda não encontrada.")
                );

        return new AgendaRetornoDTO(agenda);
    }

    public void excluir(Integer id) {

        if (!agendaRepository.existsById(id)) {
            throw new RuntimeException("Agenda não encontrada.");
        }

        agendaRepository.deleteById(id);
    }
}

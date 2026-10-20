package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.AgendaCadastroDTO;
import com.clinicamedica.main.dto.AgendaRetornoDTO;
import com.clinicamedica.main.model.Agenda;
import com.clinicamedica.main.model.Medico;
import com.clinicamedica.main.repository.AgendaRepository;
import com.clinicamedica.main.repository.ConsultaRepository;
import com.clinicamedica.main.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;

    public AgendaService(
            AgendaRepository agendaRepository,
            MedicoRepository medicoRepository,
            ConsultaRepository consultaRepository
    ) {

        this.agendaRepository = agendaRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public AgendaRetornoDTO cadastrar(AgendaCadastroDTO dados, String login) {

        if (dados == null) throw new IllegalArgumentException("Informe a data e o horário.");

        Medico medico = medicoRepository.findByUsuario_Login(login)
                .orElseThrow(() ->
                        new IllegalArgumentException("O cadastro deste médico não foi encontrado.")
                );

        if (dados.dataAgenda() == null || dados.horario() == null
                || !LocalDateTime.of(dados.dataAgenda(), dados.horario()).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Informe uma data e um horário futuros.");
        }
        if (agendaRepository.existsByMedico_IdAndDataAgendaAndHorario(medico.getId(), dados.dataAgenda(), dados.horario())) {
            throw new IllegalArgumentException("Este horário já foi cadastrado.");
        }

        Agenda agenda = new Agenda();

        agenda.setMedico(medico);
        agenda.setDataAgenda(dados.dataAgenda());
        agenda.setHorario(dados.horario());

        Agenda salva = agendaRepository.save(agenda);

        return new AgendaRetornoDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<AgendaRetornoDTO> listarDisponiveis(Integer medicoId) {

        List<Agenda> agendas = medicoId == null
                ? agendaRepository.findByDisponivelTrueOrderByDataAgendaAscHorarioAsc()
                : agendaRepository.findByDisponivelTrueAndMedico_IdOrderByDataAgendaAscHorarioAsc(medicoId);
        return agendas
                .stream()
                .filter(agenda -> LocalDateTime.of(agenda.getDataAgenda(), agenda.getHorario()).isAfter(LocalDateTime.now()))
                .map(AgendaRetornoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendaRetornoDTO> listarDoMedico(String login) {
        return agendaRepository.findByMedico_Usuario_LoginOrderByDataAgendaAscHorarioAsc(login)
                .stream().map(AgendaRetornoDTO::new).toList();
    }

    public AgendaRetornoDTO buscarPorId(Integer id) {

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Agenda não encontrada.")
                );

        return new AgendaRetornoDTO(agenda);
    }

    @Transactional
    public void excluir(Integer id, String login) {
        Agenda agenda = agendaRepository.buscarPorIdParaAtualizacao(id)
                .orElseThrow(() -> new IllegalArgumentException("Horário não encontrado."));
        if (!agenda.getMedico().getUsuario().getLogin().equals(login)) {
            throw new IllegalArgumentException("Você só pode remover horários da sua própria agenda.");
        }
        if (!agenda.isDisponivel()) {
            throw new IllegalArgumentException("Um horário reservado não pode ser removido.");
        }
        if (consultaRepository.existsByAgenda_Id(id)) {
            throw new IllegalArgumentException("Este horário possui consulta no histórico e não pode ser removido.");
        }
        agendaRepository.delete(agenda);
    }
}

package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.AtendimentoCadastroDTO;
import com.clinicamedica.main.dto.ConsultaCadastroDTO;
import com.clinicamedica.main.model.*;
import com.clinicamedica.main.repository.AgendaRepository;
import com.clinicamedica.main.repository.AtendimentoRepository;
import com.clinicamedica.main.repository.ConsultaRepository;
import com.clinicamedica.main.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegrasConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @InjectMocks
    private AtendimentoService atendimentoService;

    // Confirma que uma consulta cancelada não pode receber um atendimento.
    @Test
    void consultaCanceladaNaoPodeReceberAtendimento() {
        Consulta consulta = consulta(StatusConsulta.CANCELADO);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));

        assertThrows(IllegalArgumentException.class,
                () -> atendimentoService.registrar(new AtendimentoCadastroDTO(12, "Diagnóstico", null, null), "medico.teste"));

        verify(atendimentoRepository, never()).save(any());
    }

    // Confirma que uma consulta marcada como falta não pode receber um atendimento.
    @Test
    void faltaDoPacienteNaoPodeReceberAtendimento() {
        Consulta consulta = consulta(StatusConsulta.NAO_COMPARECEU);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));

        assertThrows(IllegalArgumentException.class,
                () -> atendimentoService.registrar(new AtendimentoCadastroDTO(12, "Diagnóstico", null, null), "medico.teste"));

        verify(atendimentoRepository, never()).save(any());
    }

    // Confirma que o cancelamento do paciente muda o status e libera um horário futuro.
    @Test
    void cancelarConsultaFuturaAtualizaStatusEDevolveHorario() {
        ConsultaService service = consultaService();
        Consulta consulta = consulta(StatusConsulta.AGENDADO);
        when(consultaRepository.bloquearPorIdEPaciente(12, "paciente.teste")).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        service.cancelar(12, "paciente.teste");

        assertEquals(StatusConsulta.CANCELADO, consulta.getStatus());
        assertTrue(consulta.getAgenda().isDisponivel());
    }

    // Confirma que um horário livre pode ser reservado com especialidade válida e observações normalizadas.
    @Test
    void agendarHorarioLivreSalvaConsultaComEspecialidadeEObservacoesNormalizadas() {
        ConsultaService service = consultaService();
        ConsultaCadastroDTO dados = new ConsultaCadastroDTO(8, 21, "  motivo do agendamento  ");
        Paciente paciente = new Paciente();
        paciente.setId(3);
        paciente.setNome("Paciente de teste");
        Agenda agenda = agendaFutura(true);
        Especialidade especialidade = new Especialidade(21, "Cardiologia");
        agenda.getMedico().setEspecialidades(List.of(especialidade));

        when(pacienteRepository.findByUsuario_Login("paciente.teste")).thenReturn(Optional.of(paciente));
        when(agendaRepository.buscarPorIdParaAtualizacao(8)).thenReturn(Optional.of(agenda));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        var retorno = service.agendar(dados, "paciente.teste");

        assertFalse(agenda.isDisponivel());
        assertEquals(StatusConsulta.AGENDADO, retorno.status());
        assertEquals(21, retorno.especialidadeId());
        assertEquals("Cardiologia", retorno.nomeEspecialidade());
        assertEquals("motivo do agendamento", retorno.observacoes());
        verify(consultaRepository).save(any(Consulta.class));
    }

    // Confirma que o sistema rejeita uma especialidade que não pertence ao médico do horário.
    @Test
    void naoPermiteAgendarComEspecialidadeQueNaoPertenceAoMedico() {
        ConsultaService service = consultaService();
        Paciente paciente = new Paciente();
        Agenda agenda = agendaFutura(true);
        agenda.getMedico().setEspecialidades(List.of(new Especialidade(21, "Cardiologia")));
        when(pacienteRepository.findByUsuario_Login("paciente.teste")).thenReturn(Optional.of(paciente));
        when(agendaRepository.buscarPorIdParaAtualizacao(8)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalArgumentException.class,
                () -> service.agendar(new ConsultaCadastroDTO(8, 22, null), "paciente.teste"));

        assertTrue(agenda.isDisponivel());
        verify(consultaRepository, never()).save(any());
    }

    // Confirma que um horário já ocupado não pode ser reservado novamente.
    @Test
    void naoPermiteReservarHorarioQueJaEstaOcupado() {
        ConsultaService service = consultaService();
        Paciente paciente = new Paciente();
        Agenda agenda = agendaFutura(false);
        when(pacienteRepository.findByUsuario_Login("paciente.teste")).thenReturn(Optional.of(paciente));
        when(agendaRepository.buscarPorIdParaAtualizacao(8)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalArgumentException.class,
                () -> service.agendar(new ConsultaCadastroDTO(8, 21, null), "paciente.teste"));

        verify(consultaRepository, never()).save(any());
    }

    // Confirma que o paciente não pode cancelar uma consulta cujo horário já passou.
    @Test
    void naoPermiteCancelarConsultaQueJaPassou() {
        ConsultaService service = consultaService();
        Consulta consulta = consulta(StatusConsulta.AGENDADO, LocalDateTime.now().minusMinutes(5), false);
        when(consultaRepository.bloquearPorIdEPaciente(12, "paciente.teste")).thenReturn(Optional.of(consulta));

        assertThrows(IllegalArgumentException.class, () -> service.cancelar(12, "paciente.teste"));

        assertEquals(StatusConsulta.AGENDADO, consulta.getStatus());
        assertFalse(consulta.getAgenda().isDisponivel());
        verify(consultaRepository, never()).save(any());
    }

    // Confirma que o médico libera o horário ao cancelar uma consulta futura, mas não um horário passado.
    @Test
    void cancelamentoDoMedicoLiberaSomenteHorarioFuturo() {
        ConsultaService service = consultaService();
        Consulta futura = consulta(StatusConsulta.AGENDADO, LocalDateTime.now().plusDays(1), false);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(futura));
        when(consultaRepository.save(futura)).thenReturn(futura);

        service.cancelarPeloMedico(12, "medico.teste");

        assertEquals(StatusConsulta.CANCELADO, futura.getStatus());
        assertTrue(futura.getAgenda().isDisponivel());

        Consulta passada = consulta(StatusConsulta.AGENDADO, LocalDateTime.now().minusDays(1), false);
        when(consultaRepository.bloquearPorIdEMedico(13, "medico.teste")).thenReturn(Optional.of(passada));
        when(consultaRepository.save(passada)).thenReturn(passada);

        service.cancelarPeloMedico(13, "medico.teste");

        assertEquals(StatusConsulta.CANCELADO, passada.getStatus());
        assertFalse(passada.getAgenda().isDisponivel());
    }

    // Confirma que a falta só pode ser registrada após o horário e que o horário não é liberado.
    @Test
    void faltaNaoPodeSerRegistradaAntesDoHorarioEQuandoRegistradaNaoLiberaSlot() {
        ConsultaService service = consultaService();
        Consulta futura = consulta(StatusConsulta.AGENDADO, LocalDateTime.now().plusHours(1), false);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(futura));

        assertThrows(IllegalArgumentException.class, () -> service.marcarNaoCompareceu(12, "medico.teste"));

        assertEquals(StatusConsulta.AGENDADO, futura.getStatus());
        verify(consultaRepository, never()).save(any());

        Consulta passada = consulta(StatusConsulta.AGENDADO, LocalDateTime.now().minusMinutes(1), false);
        when(consultaRepository.bloquearPorIdEMedico(13, "medico.teste")).thenReturn(Optional.of(passada));
        when(consultaRepository.save(passada)).thenReturn(passada);

        service.marcarNaoCompareceu(13, "medico.teste");

        assertEquals(StatusConsulta.NAO_COMPARECEU, passada.getStatus());
        assertFalse(passada.getAgenda().isDisponivel());
    }

    // Confirma que registrar um atendimento conclui a consulta e muda seu status para REALIZADO.
    @Test
    void atendimentoConcluidoAtualizaConsultaParaRealizado() {
        Consulta consulta = consulta(StatusConsulta.AGENDADO);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));
        when(atendimentoRepository.existsByConsulta_Id(12)).thenReturn(false);
        when(atendimentoRepository.save(any(Atendimento.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        atendimentoService.registrar(new AtendimentoCadastroDTO(12, "  Diagnóstico  ", "  Prescrição  ", null), "medico.teste");

        assertEquals(StatusConsulta.REALIZADO, consulta.getStatus());
        verify(atendimentoRepository).save(any(Atendimento.class));
        verify(consultaRepository).save(consulta);
    }

    // Confirma que o sistema impede dois atendimentos vinculados à mesma consulta.
    @Test
    void naoPermiteRegistrarDoisAtendimentosParaMesmaConsulta() {
        Consulta consulta = consulta(StatusConsulta.AGENDADO);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));
        when(atendimentoRepository.existsByConsulta_Id(12)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> atendimentoService.registrar(new AtendimentoCadastroDTO(12, "Diagnóstico", null, null), "medico.teste"));

        assertEquals(StatusConsulta.AGENDADO, consulta.getStatus());
        verify(atendimentoRepository, never()).save(any());
        verify(consultaRepository, never()).save(any());
    }

    private ConsultaService consultaService() {
        return new ConsultaService(consultaRepository, agendaRepository, pacienteRepository);
    }

    private Agenda agendaFutura(boolean disponivel) {
        Usuario usuarioMedico = new Usuario();
        usuarioMedico.setLogin("medico.teste");
        Medico medico = new Medico();
        medico.setId(5);
        medico.setNome("Médico de teste");
        medico.setUsuario(usuarioMedico);
        LocalDateTime futuro = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        return new Agenda(8, medico, futuro.toLocalDate(), futuro.toLocalTime(), disponivel);
    }

    private Consulta consulta(StatusConsulta status) {
        return consulta(status, LocalDateTime.now().plusDays(1).withSecond(0).withNano(0), false);
    }

    private Consulta consulta(StatusConsulta status, LocalDateTime dataHora, boolean disponivel) {
        Usuario usuarioMedico = new Usuario();
        usuarioMedico.setLogin("medico.teste");
        Medico medico = new Medico();
        medico.setId(5);
        medico.setNome("Médico de teste");
        medico.setUsuario(usuarioMedico);

        Agenda agenda = new Agenda(8, medico, dataHora.toLocalDate(), dataHora.toLocalTime(), disponivel);
        Paciente paciente = new Paciente();
        paciente.setId(3);
        paciente.setNome("Paciente de teste");

        Consulta consulta = new Consulta();
        consulta.setId(12);
        consulta.setAgenda(agenda);
        consulta.setPaciente(paciente);
        consulta.setStatus(status);
        return consulta;
    }
}

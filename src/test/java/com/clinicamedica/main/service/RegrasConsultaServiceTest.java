package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.AtendimentoCadastroDTO;
import com.clinicamedica.main.model.*;
import com.clinicamedica.main.repository.AtendimentoRepository;
import com.clinicamedica.main.repository.ConsultaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private AtendimentoRepository atendimentoRepository;

    @InjectMocks
    private AtendimentoService atendimentoService;

    @Test
    void consultaCanceladaNaoPodeReceberAtendimento() {
        Consulta consulta = consulta(StatusConsulta.CANCELADO);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));

        assertThrows(IllegalArgumentException.class,
                () -> atendimentoService.registrar(new AtendimentoCadastroDTO(12, "Diagnóstico", null, null), "medico.teste"));

        verify(atendimentoRepository, never()).save(any());
    }

    @Test
    void faltaDoPacienteNaoPodeReceberAtendimento() {
        Consulta consulta = consulta(StatusConsulta.NAO_COMPARECEU);
        when(consultaRepository.bloquearPorIdEMedico(12, "medico.teste")).thenReturn(Optional.of(consulta));

        assertThrows(IllegalArgumentException.class,
                () -> atendimentoService.registrar(new AtendimentoCadastroDTO(12, "Diagnóstico", null, null), "medico.teste"));

        verify(atendimentoRepository, never()).save(any());
    }

    @Test
    void cancelarConsultaFuturaAtualizaStatusEDevolveHorario() {
        ConsultaService service = new ConsultaService(consultaRepository, null, null);
        Consulta consulta = consulta(StatusConsulta.AGENDADO);
        when(consultaRepository.bloquearPorIdEPaciente(12, "paciente.teste")).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        service.cancelar(12, "paciente.teste");

        assertEquals(StatusConsulta.CANCELADO, consulta.getStatus());
        assertTrue(consulta.getAgenda().isDisponivel());
    }

    private Consulta consulta(StatusConsulta status) {
        Usuario usuarioMedico = new Usuario();
        usuarioMedico.setLogin("medico.teste");
        Medico medico = new Medico();
        medico.setId(5);
        medico.setNome("Médico de teste");
        medico.setUsuario(usuarioMedico);

        Agenda agenda = new Agenda(8, medico, LocalDate.now().plusDays(1), LocalTime.of(10, 0), false);
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

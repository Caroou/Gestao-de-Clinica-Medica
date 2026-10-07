package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Agenda;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    List<Agenda> findByDisponivelTrueOrderByDataAgendaAscHorarioAsc();

    List<Agenda> findByDisponivelTrueAndMedico_IdOrderByDataAgendaAscHorarioAsc(Integer medicoId);

    List<Agenda> findByMedico_Usuario_LoginOrderByDataAgendaAscHorarioAsc(String login);

    boolean existsByMedico_IdAndDataAgendaAndHorario(Integer medicoId, LocalDate dataAgenda, LocalTime horario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select agenda from Agenda agenda where agenda.id = :id")
    Optional<Agenda> buscarPorIdParaAtualizacao(@Param("id") Integer id);
}

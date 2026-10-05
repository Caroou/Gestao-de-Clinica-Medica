package com.clinicamedica.repository;

import com.clinicamedica.model.Consulta;
import com.clinicamedica.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    boolean existsByMedicoIdAndDataHoraAndStatusNot(Integer medicoId, LocalDateTime dataHora, StatusConsulta status);

    List<Consulta> findByMedicoId(Integer medicoId);

    List<Consulta> findByPacienteId(Integer pacienteId);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByStatus(StatusConsulta status);
}

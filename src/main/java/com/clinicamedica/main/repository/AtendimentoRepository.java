package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Integer> {

    boolean existsByConsulta_Id(Integer consultaId);

    Optional<Atendimento> findByConsulta_Id(Integer consultaId);

    @Query("select a from Atendimento a join fetch a.consulta c join fetch c.agenda agenda join fetch agenda.medico medico where c.paciente.usuario.login = :login order by a.dataHora desc")
    List<Atendimento> listarDoPaciente(@Param("login") String login);

    @Query("select a from Atendimento a join fetch a.consulta c join fetch c.paciente paciente join fetch c.agenda agenda join fetch agenda.medico medico where medico.usuario.login = :login order by a.dataHora desc")
    List<Atendimento> listarDoMedico(@Param("login") String login);
}

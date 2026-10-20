package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Consulta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    @Query("select c from Consulta c join fetch c.agenda a join fetch a.medico m join fetch c.paciente p left join fetch c.especialidade where p.usuario.login = :login order by a.dataAgenda, a.horario")
    List<Consulta> listarDoPaciente(@Param("login") String login);

    @Query("select c from Consulta c join fetch c.agenda a join fetch a.medico m join fetch c.paciente p left join fetch c.especialidade where m.usuario.login = :login order by a.dataAgenda, a.horario")
    List<Consulta> listarDoMedico(@Param("login") String login);

    Optional<Consulta> findByIdAndPaciente_Usuario_Login(Integer id, String login);

    Optional<Consulta> findByIdAndAgenda_Medico_Usuario_Login(Integer id, String login);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Consulta c join c.paciente p where c.id = :id and p.usuario.login = :login")
    Optional<Consulta> bloquearPorIdEPaciente(@Param("id") Integer id, @Param("login") String login);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Consulta c join c.agenda a join a.medico m where c.id = :id and m.usuario.login = :login")
    Optional<Consulta> bloquearPorIdEMedico(@Param("id") Integer id, @Param("login") String login);

    List<Consulta> findByAgenda_DataAgenda(LocalDate data);

    boolean existsByEspecialidade_Id(Integer especialidadeId);

    boolean existsByAgenda_Id(Integer agendaId);
}

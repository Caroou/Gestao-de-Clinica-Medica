package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    boolean existsByCrm(String crm);

    boolean existsByCrmAndIdNot(String crm, Integer id);

    Optional<Medico> findByUsuario_Login(String login);

}

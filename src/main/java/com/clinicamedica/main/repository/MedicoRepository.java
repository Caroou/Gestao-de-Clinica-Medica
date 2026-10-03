package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    boolean existsByCrm(String crm);

}
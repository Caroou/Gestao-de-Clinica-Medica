package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Integer> {

    Optional<Especialidade> findByNome(String nome);

    boolean existsByNome(String nome);
}

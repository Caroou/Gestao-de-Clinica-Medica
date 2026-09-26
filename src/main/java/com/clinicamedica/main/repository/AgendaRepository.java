package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Integer>{
}

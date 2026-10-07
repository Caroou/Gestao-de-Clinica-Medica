package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    // O Spring cria o SQL automático: SELECT count(*) FROM paciente WHERE cpf = ?
    boolean existsByCpf(String cpf);

    // O Spring cria o SQL automático: SELECT count(*) FROM paciente WHERE email = ?
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Optional<Paciente> findByUsuario_Login(String login);

}

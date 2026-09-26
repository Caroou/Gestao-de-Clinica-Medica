package com.clinicamedica.main.repository;

import com.clinicamedica.main.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//O JpaRepository já vem com cadastrar(), buscarPorId(), deletar() prontos.
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLoginAndAtivoTrue(String login);
}
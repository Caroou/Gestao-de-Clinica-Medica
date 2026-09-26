package com.clinicamedica.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Desliga o CSRF (permite enviar formulários e JSON sem token no início)
                .csrf(csrf -> csrf.disable())

                // 2. MATA a tela HTML de login do Spring (a que está te travando)
                .formLogin(form -> form.disable())

                // 3. MATA o popup de login nativo do navegador
                .httpBasic(basic -> basic.disable())

                // 4. Configura as portas e rotas
                .authorizeHttpRequests(auth -> auth
                        // Libera as telas da nossa interface
                        .requestMatchers("/", "/index.html").permitAll()

                        // Libera a API para receber o cadastro e login do usuário
                        .requestMatchers(
                                "/api/usuarios/cadastrar",
                                "/api/usuarios/login",
                                "/api/pacientes/**",
                                "/",              // Libera a raiz (localhost:8080)
                                "/*.html",        // Libera qualquer arquivo HTML (index.html, cadastro.html)
                                "/css/**",        // Libera tudo dentro da pasta css
                                "/js/**"          // Libera tudo dentro da pasta js
                        ).permitAll()

                        // O resto fica trancado
                        .anyRequest().authenticated()
                )
                .build();
    }
}
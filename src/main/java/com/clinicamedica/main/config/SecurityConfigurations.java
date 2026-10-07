package com.clinicamedica.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfigurations {

    @Bean
    public FilterRegistrationBean<SessionAuthenticationFilter> sessionAuthenticationFilterRegistration(
            SessionAuthenticationFilter filter) {
        FilterRegistrationBean<SessionAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionAuthenticationFilter sessionAuthenticationFilter) throws Exception {
        return http
                // 1. Desliga o CSRF (permite enviar formulários e JSON sem token no início)
                .csrf(csrf -> csrf.disable())

                // 2. MATA a tela HTML de login do Spring (a que está te travando)
                .formLogin(form -> form.disable())

                // 3. MATA o popup de login nativo do navegador
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))

                // 4. Cadastro/login são públicos; os demais dados exigem sessão e perfil apropriado.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/pages/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar", "/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").hasRole("PACIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/medicos").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/especialidades", "/api/especialidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/medicos", "/api/medicos/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/agendas/disponiveis").hasAnyRole("ADMIN", "PACIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/agendas/minhas").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/api/agendas").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/agendas/**").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/agendas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/consultas").hasRole("PACIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/consultas/minhas").hasRole("PACIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/consultas/medico/minhas").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.PUT, "/api/consultas/*/reagendar", "/api/consultas/*/cancelar").hasRole("PACIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/consultas/*/cancelar-medico", "/api/consultas/*/nao-compareceu").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/api/atendimentos").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/atendimentos/meus").hasRole("PACIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/atendimentos/medico/meus").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/consultas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers("/api/pacientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/especialidades").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/medicos/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/sessao", "/api/usuarios/logout").authenticated()
                        .requestMatchers("/api/**").denyAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(sessionAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .build();
    }
}

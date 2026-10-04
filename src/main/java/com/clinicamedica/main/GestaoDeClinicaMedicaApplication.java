package com.clinicamedica.main;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GestaoDeClinicaMedicaApplication {

	public static void main(String[] args) {
		boolean modoCriarAdmin = java.util.Arrays.asList(args).contains("--criar-admin");
		if (!modoCriarAdmin) {
			SpringApplication.run(GestaoDeClinicaMedicaApplication.class, args);
			return;
		}

		SpringApplication application = new SpringApplication(GestaoDeClinicaMedicaApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);

		System.out.println("Sistema de Gestão funcionando em localhost:8080");
		
		try (ConfigurableApplicationContext ignored = application.run(args)) {
			// O comando termina assim que concluir o provisionamento, sem iniciar o servidor web.
		}
	}
}

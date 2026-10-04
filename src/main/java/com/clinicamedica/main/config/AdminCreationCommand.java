package com.clinicamedica.main.config;

import com.clinicamedica.main.service.AdminProvisioningService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.util.Arrays;

/** Interactive, explicitly invoked account creation command. */
@Component
public class AdminCreationCommand implements ApplicationRunner {

    private final AdminProvisioningService adminProvisioningService;

    public AdminCreationCommand(AdminProvisioningService adminProvisioningService) {
        this.adminProvisioningService = adminProvisioningService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("criar-admin")) {
            return;
        }

        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException(
                    "Execute --criar-admin em um terminal interativo para que a senha não apareça na tela.");
        }

        String login = console.readLine("Login do novo admin: ");
        char[] senha = console.readPassword("Senha (15 a 72 bytes UTF-8): ");
        char[] confirmacao = console.readPassword("Confirme a senha: ");

        try {
            if (senha == null || confirmacao == null) {
                throw new IllegalStateException("Entrada encerrada. Nenhuma conta foi criada.");
            }
            if (!Arrays.equals(senha, confirmacao)) {
                throw new IllegalArgumentException("As senhas não coincidem. Nenhuma conta foi criada.");
            }

            adminProvisioningService.criarAdministrador(login, new String(senha));
            console.printf("Conta ADMIN criada para o login '%s'.%n", login.trim());
        } finally {
            if (senha != null) Arrays.fill(senha, '\0');
            if (confirmacao != null) Arrays.fill(confirmacao, '\0');
        }
    }
}

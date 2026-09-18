package com.clinicamedica.util;

import com.clinicamedica.dao.UsuarioDAO;
import com.clinicamedica.model.Usuario;

public class TesteUsuarioDAO {
    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();

        System.out.println("--- 1. TESTE DE CADASTRO ---");
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin("admin_teste");
        novoUsuario.setSenha("senhaSegura123");
        novoUsuario.setPerfil("Administrador");
        novoUsuario.setAutorizado(true);

        boolean cadastrado = dao.cadastrar(novoUsuario);
        if (cadastrado) {
            System.out.println("Utilizador cadastrado com sucesso! Verifica no MySQL se a palavra-passe está em formato hash.");
        } else {
            System.out.println("Falha ao cadastrar utilizador.");
        }

        System.out.println("\n--- 2. TESTE DE AUTENTICAÇÃO BEM-SUCEDIDA ---");
        // Simula o utilizador a digitar os dados corretos no ecrã de login
        Usuario autenticado = dao.autenticar("admin_teste", "senhaSegura123");
        
        if (autenticado != null) {
            System.out.println("Login efetuado com sucesso!");
            System.out.println("Bem-vindo, " + autenticado.getLogin() + " | Perfil: " + autenticado.getPerfil());
        } else {
            System.out.println("Acesso negado. Erro na validação.");
        }
        
        System.out.println("\n--- 3. TESTE DE FALHA DE SEGURANÇA ---");
        // Simula uma tentativa de invasão com palavra-passe incorreta
        Usuario invasor = dao.autenticar("admin_teste", "senhaErrada");
        if (invasor == null) {
            System.out.println("O sistema bloqueou a tentativa de acesso invasivo corretamente.");
        }
    }
}
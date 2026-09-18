package com.clinicamedica.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        System.out.println("A iniciar o teste de ligação ao MySQL...");
        
        try {
            // Chama o método de ligação da tua classe ConexaoBanco
            Connection conexao = ConexaoBanco.conectar();
            
            if (conexao != null) {
                System.out.println("Ligação bem-sucedida! O Java está a comunicar com a base de dados.");
                // É uma boa prática fechar a ligação após o teste
                conexao.close(); 
            }
        } catch (SQLException e) {
            System.out.println("Falha na ligação à base de dados.");
            System.out.println("Erro detalhado: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro de execução: " + e.getMessage());
        }
    }
}
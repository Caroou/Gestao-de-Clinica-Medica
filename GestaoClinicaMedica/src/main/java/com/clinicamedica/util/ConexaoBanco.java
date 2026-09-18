package com.clinicamedica.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    
    // Ajuste a porta 3306 e o nome do banco se necessário
    private static final String URL = "jdbc:mysql://localhost:3306/clinica_medica?useTimezone=true&serverTimezone=UTC";
    private static final String USER = "root"; // coloque o seu usuário do MySQL
    private static final String PASSWORD = "Senha@123"; // coloque a sua senha
    
    public static Connection conectar() {
        try {
            // Tenta abrir a porta com o banco
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // Se o MySQL estiver desligado ou a senha errada, ele grita aqui
            throw new RuntimeException("Deu ruim na conexão com o banco: " + e.getMessage());
        }
    }
}

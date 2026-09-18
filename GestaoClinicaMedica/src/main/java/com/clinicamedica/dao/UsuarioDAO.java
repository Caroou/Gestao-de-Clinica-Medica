package com.clinicamedica.dao;

import com.clinicamedica.model.Usuario;
import com.clinicamedica.util.ConexaoBanco;
import com.clinicamedica.util.SegurancaHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean cadastrar(Usuario usuario) {
        // A interrogação (?) previne falhas de segurança (SQL Injection)
        String sql = "INSERT INTO usuario (login, senha, perfil, autorizado) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getLogin());
            
            // Chamamos o utilitário para converter a senha limpa num Hash irreversível
            String hashSenha = SegurancaHelper.gerarHashSenha(usuario.getSenha());
            stmt.setString(2, hashSenha);
            
            stmt.setString(3, usuario.getPerfil());
            stmt.setBoolean(4, usuario.isAutorizado());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao registar utilizador: " + e.getMessage());
            return false;
        }
    }

    public Usuario autenticar(String login, String senhaDigitada) {
        // Procuramos o utilizador apenas pelo login
        String sql = "SELECT * FROM usuario WHERE login = ?";

        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            // Se encontrou o login na base de dados
            if (rs.next()) {
                String hashDoBanco = rs.getString("senha");

                // Cruza a palavra-passe digitada com o Hash guardado
                if (SegurancaHelper.verificarSenha(senhaDigitada, hashDoBanco)) {
                    
                    Usuario usuarioAutenticado = new Usuario();
                    usuarioAutenticado.setId(rs.getInt("id"));
                    usuarioAutenticado.setLogin(rs.getString("login"));
                    usuarioAutenticado.setPerfil(rs.getString("perfil"));
                    usuarioAutenticado.setAutorizado(rs.getBoolean("autorizado"));
                    
                    return usuarioAutenticado;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar utilizador: " + e.getMessage());
        }
        
        // Se a palavra-passe não bater ou o login não existir, devolve nulo
        return null;
    }
}

package com.senacsoluctions.dao;

import com.senacsoluctions.model.Usuario; 
import org.mindrot.jbcrypt.BCrypt; // Importa o BCrypt
import java.sql.*;

public class UsuarioDao {

    public Usuario autenticarUsuario(String login, String senhaDigitada) {
        // 1. Busca APENAS pelo login
        String sql = "SELECT * FROM usuario WHERE login = ?";
        
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, login);
            
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    // Pegamos a senha mascarada que está guardada no Supabase
                    String senhaBanco = rs.getString("senha");
                    
                    // 2. O BCrypt faz a mágica de conferir a senha digitada com o hash do banco
                    if (BCrypt.checkpw(senhaDigitada, senhaBanco)) {
                        // Se bater, monta o objeto do usuário e autoriza o login
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setLogin(rs.getString("login"));
                        usuario.setCargo(rs.getString("cargo"));
                        return usuario;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
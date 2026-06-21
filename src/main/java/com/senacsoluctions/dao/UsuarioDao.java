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

    public boolean salvarUsuario(Usuario usuario) {

        String sql = "INSERT INTO usuario (nome, login, senha, cargo) VALUES (?, ?, ?, ?::cargo_usuario)";

        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getCargo());

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário no DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE LOWER(login) = LOWER(?)";

        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Instancia o modelo e preenche com os dados vindos das colunas do banco
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id")); 
                    usuario.setNome(rs.getString("nome"));
                    usuario.setLogin(rs.getString("login"));
                    usuario.setCargo(rs.getString("cargo"));

                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por login no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Retorna null caso o usuário não exista ou ocorra um erro
    }
}
package com.senacsoluctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.senacsoluctions.controler.ClienteControler;
import com.senacsoluctions.model.Cliente;

public class ClienteDao {

    public boolean salvarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar cliente no DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public Cliente buscarPorCpf(String cpf) {
        
        String sql = "SELECT * FROM cliente WHERE cpf = ?";
        
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ClienteControler.apenasNumeros(cpf));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rs.getInt("id_cliente"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setCpf(rs.getString("cpf"));
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setEmail(rs.getString("email"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por CPF: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> buscarPorNome(String nomeBusca) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome ILIKE ?"; // ILIKE ignora maiúsculas/minúsculas no Postgres

        try (Connection conn = ConexaoBanco.getConexao();
                PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, "%" + nomeBusca + "%");
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                c.setTelefone(rs.getString("telefone"));
                c.setEmail(rs.getString("email"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
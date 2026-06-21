package com.senacsoluctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.senacsoluctions.model.OrdemServico;

public class OrdemServicoDao {

    public boolean salvarOS(OrdemServico os) {
        String sql = "INSERT INTO ordem_servico (data_abertura, data_limite, custo, observacoes, id_cliente, id_equipamento) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Convertendo as datas do Java (java.util.Date) para o formato do banco (java.sql.Date)
            stmt.setDate(1, new java.sql.Date(os.getDataAbertura().getTime()));
            stmt.setDate(2, new java.sql.Date(os.getDataLimite().getTime()));
            stmt.setDouble(3, os.getCusto());
            stmt.setString(4, os.getObservacoes());
            
            stmt.setInt(5, os.getCliente().getIdCliente()); 
            stmt.setInt(6, os.getEquipamento().getIdEquipamento());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                // Recupera o Número da OS gerado pelo Supabase
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        os.setNumOs(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar Ordem de Serviço: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
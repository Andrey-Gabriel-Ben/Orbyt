package com.senacsoluctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.senacsoluctions.model.Equipamento;

public class EquipamentoDao {

    public boolean salvar(Equipamento equip) {
        String sql = "INSERT INTO equipamento (tipo, marca, modelo, num_serie, id_cliente) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equip.getTipo());
            stmt.setString(2, equip.getMarca());
            stmt.setString(3, equip.getModelo());
            stmt.setString(4, equip.getNumSerie());
            stmt.setInt(5, equip.getCliente().getIdCliente());
            
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        equip.setIdEquipamento(rs.getInt("id_equipamento"));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Equipamento> listarPorCliente(int idCliente) {
        List<Equipamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipamento WHERE id_cliente = ?";
        
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipamento eq = new Equipamento();
                    eq.setIdEquipamento(rs.getInt("id_equipamento"));
                    eq.setTipo(rs.getString("tipo"));
                    eq.setMarca(rs.getString("marca"));
                    eq.setModelo(rs.getString("modelo"));
                    eq.setNumSerie(rs.getString("num_serie"));
                    lista.add(eq);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
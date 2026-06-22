package com.senacsoluctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.StatusOS;
import com.senacsoluctions.model.Usuario;

public class OrdemServicoDao {

    // Consulta base usada por todas as listagens: já traz cliente, equipamento
    // e técnico (quando houver) prontos, evitando N+1 select.
    private static final String SELECT_BASE = "SELECT os.num_os, os.data_abertura, os.data_limite, os.status, os.defeito, "
            + "       os.custo, os.observacoes, os.id_cliente, os.id_equipamento, os.id_tecnico, "
            + "       c.nome AS cli_nome, c.cpf AS cli_cpf, c.telefone AS cli_telefone, c.email AS cli_email, "
            + "       e.tipo AS eq_tipo, e.marca AS eq_marca, e.modelo AS eq_modelo, e.num_serie AS eq_num_serie, "
            + "       t.nome AS tec_nome, t.login AS tec_login, t.cargo AS tec_cargo "
            + "FROM ordem_servico os "
            + "JOIN cliente c ON c.id_cliente = os.id_cliente "
            + "JOIN equipamento e ON e.id_equipamento = os.id_equipamento "
            + "LEFT JOIN usuario t ON t.id_usuario = os.id_tecnico ";

    public boolean salvarOS(OrdemServico os) {
        String sql = "INSERT INTO ordem_servico "
                + "(data_abertura, data_limite, status, defeito, custo, observacoes, id_cliente, id_equipamento) "
                + "VALUES (?, ?, ?::status_os, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, new java.sql.Timestamp(os.getDataAbertura().getTime()));
            stmt.setDate(2, new java.sql.Date(os.getDataLimite().getTime()));
            stmt.setString(3, os.getStatus() != null ? os.getStatus() : StatusOS.EM_ABERTO);
            stmt.setString(4, os.getDescricaoDefeito());
            stmt.setDouble(5, os.getCusto());
            stmt.setString(6, os.getObservacoes());
            stmt.setInt(7, os.getCliente().getIdCliente());
            stmt.setInt(8, os.getEquipamento().getIdEquipamento());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
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

    /**
     * T1: OS já finalizadas pelo técnico, aguardando o cliente retirar o
     * equipamento.
     */
    public List<OrdemServico> listarFinalizadasPendentesEntrega() {
        String sql = SELECT_BASE + "WHERE os.status = '" + StatusOS.FINALIZADA + "'::status_os "
                + "ORDER BY os.data_abertura ASC";
        return listar(sql, null);
    }

    /**
     * T2: OS de um cliente específico que ainda não estão finalizadas nem
     * concluídas.
     */
    public List<OrdemServico> listarPendentesPorCliente(int idCliente) {
        String sql = SELECT_BASE + "WHERE os.id_cliente = ? "
                + "AND os.status NOT IN ('" + StatusOS.FINALIZADA + "'::status_os, '" + StatusOS.CONCLUIDA
                + "'::status_os) "
                + "ORDER BY os.data_limite ASC";
        return listar(sql, stmt -> stmt.setInt(1, idCliente));
    }

    /** T3 (opção 1): OS que o técnico logado já assumiu e ainda está atendendo. */
    public List<OrdemServico> listarPendentesPorTecnico(int idTecnico) {
        String sql = SELECT_BASE + "WHERE os.id_tecnico = ? AND os.status = '" + StatusOS.EM_ANDAMENTO + "'::status_os "
                + "ORDER BY os.data_limite ASC";
        return listar(sql, stmt -> stmt.setInt(1, idTecnico));
    }

    /**
     * T3 (opção 2): fila de OS em aberto, sem técnico ainda, ordenada por prazo
     * (estilo Overcooked).
     */
    public List<OrdemServico> listarDisponiveisParaTecnico() {
        String sql = SELECT_BASE + "WHERE os.status = '" + StatusOS.EM_ABERTO
                + "'::status_os AND os.id_tecnico IS NULL "
                + "ORDER BY os.data_limite ASC";
        return listar(sql, null);
    }

    public OrdemServico buscarPorNumOs(int numOs) {
        String sql = SELECT_BASE + "WHERE os.num_os = ?";
        List<OrdemServico> resultado = listar(sql, stmt -> stmt.setInt(1, numOs));
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /**
     * Usada na tela de detalhe (T3) para o técnico atualizar as observações do
     * atendimento.
     */
    public boolean atualizarObservacoes(int numOs, String observacoes) {
        String sql = "UPDATE ordem_servico SET observacoes = ? WHERE num_os = ?";
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, observacoes);
            stmt.setInt(2, numOs);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar observações da OS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * O técnico "pega" uma OS da fila: vincula o id_tecnico e avança o status para
     * EM_ANDAMENTO.
     * A condição id_tecnico IS NULL evita que dois técnicos assumam a mesma OS ao
     * mesmo tempo.
     */
    public boolean assumirOS(int numOs, int idTecnico) {
        String sql = "UPDATE ordem_servico SET id_tecnico = ?, status = ?::status_os "
                + "WHERE num_os = ? AND id_tecnico IS NULL";
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTecnico);
            stmt.setString(2, StatusOS.EM_ANDAMENTO);
            stmt.setInt(3, numOs);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao assumir a OS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Muda o status da OS para FINALIZADA (técnico concluiu o reparo).
     * O status CONCLUIDA é reservado para quando o atendente confirma a
     * retirada pelo cliente (ver marcarComoConcluida).
     */
    public boolean finalizarOS(int numOs) {
        String sql = "UPDATE ordem_servico SET status = ?::status_os WHERE num_os = ?";
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, StatusOS.FINALIZADA);
            stmt.setInt(2, numOs);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao finalizar a OS #" + numOs + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Atendente confirma a retirada do equipamento pelo cliente (T1). */
    public boolean marcarComoConcluida(int numOs) {
        String sql = "UPDATE ordem_servico SET status = ?::status_os WHERE num_os = ?";
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, StatusOS.CONCLUIDA);
            stmt.setInt(2, numOs);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao concluir a OS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Infraestrutura interna de mapeamento/consulta compartilhada
    // ---------------------------------------------------------------

    @FunctionalInterface
    private interface PreparadorDeParametros {
        void preparar(PreparedStatement stmt) throws SQLException;
    }

    private List<OrdemServico> listar(String sql, PreparadorDeParametros preparador) {
        List<OrdemServico> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (preparador != null) {
                preparador.preparar(stmt);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLinha(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar Ordens de Serviço: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private OrdemServico mapearLinha(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNome(rs.getString("cli_nome"));
        cliente.setCpf(rs.getString("cli_cpf"));
        cliente.setTelefone(rs.getString("cli_telefone"));
        cliente.setEmail(rs.getString("cli_email"));

        Equipamento equipamento = new Equipamento();
        equipamento.setIdEquipamento(rs.getInt("id_equipamento"));
        equipamento.setTipo(rs.getString("eq_tipo"));
        equipamento.setMarca(rs.getString("eq_marca"));
        equipamento.setModelo(rs.getString("eq_modelo"));
        equipamento.setNumSerie(rs.getString("eq_num_serie"));
        equipamento.setCliente(cliente);

        Usuario tecnico = null;
        int idTecnico = rs.getInt("id_tecnico");
        if (!rs.wasNull()) {
            tecnico = new Usuario();
            tecnico.setIdUsuario(idTecnico);
            tecnico.setNome(rs.getString("tec_nome"));
            tecnico.setLogin(rs.getString("tec_login"));
            tecnico.setCargo(rs.getString("tec_cargo"));
        }

        OrdemServico os = new OrdemServico();
        os.setNumOs(rs.getInt("num_os"));
        os.setDataAbertura(rs.getTimestamp("data_abertura"));
        os.setDataLimite(rs.getDate("data_limite"));
        os.setStatus(rs.getString("status"));
        os.setDescricaoDefeito(rs.getString("defeito"));
        os.setCusto(rs.getDouble("custo"));
        os.setObservacoes(rs.getString("observacoes"));
        os.setCliente(cliente);
        os.setEquipamento(equipamento);
        os.setTecnico(tecnico);
        return os;
    }
}

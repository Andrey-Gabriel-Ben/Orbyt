package com.senacsoluctions.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.senacsoluctions.controler.OrdemServicoControler;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.Usuario;

/**
 * T3 (opção 2): "lista as ordens de serviço pendentes por data de entrega, mudando a cor
 * dependendo da proximidade com a data atual" - a fila de OS ainda sem técnico, no
 * estilo de prioridade visual descrito no documento de requisitos ("estilo Overcooked").
 */
public class TelaIniciarOS extends JFrame {

    private final Usuario tecnicoLogado;
    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<OrdemServico> listaAtual;

    public TelaIniciarOS(Usuario tecnico) {
        this.tecnicoLogado = tecnico;

        setTitle("Orbyt - Fila de Atendimento");
        setSize(760, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("OS aguardando técnico, ordenadas por prazo de entrega", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelLegenda = new JPanel();
        painelLegenda.add(criarLegenda("Verde: 11 a 15 dias", new Color(60, 160, 90)));
        painelLegenda.add(criarLegenda("Amarelo: 6 a 10 dias", new Color(210, 170, 30)));
        painelLegenda.add(criarLegenda("Vermelho: 0 a 5 dias / atrasado", new Color(190, 60, 60)));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(lblTitulo, BorderLayout.NORTH);
        painelTopo.add(painelLegenda, BorderLayout.SOUTH);
        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = { "Nº OS", "Cliente", "Equipamento", "Defeito Relatado", "Prazo Limite", "Dias Restantes" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setDefaultRenderer(Object.class, new RendererDePrioridade());
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnIniciar = new JButton("Iniciar Atendimento desta OS");
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnIniciar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarDados());
        btnIniciar.addActionListener(e -> iniciarAtendimento());

        carregarDados();
    }

    private JLabel criarLegenda(String texto, Color cor) {
        JLabel lbl = new JLabel("● " + texto);
        lbl.setForeground(cor);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private void carregarDados() {
        listaAtual = osControler.listarDisponiveisParaTecnico();
        modeloTabela.setRowCount(0);

        for (OrdemServico os : listaAtual) {
            long dias = Utils.diasRestantes(os.getDataLimite());
            modeloTabela.addRow(new Object[] {
                    os.getNumOs(),
                    os.getCliente().getNome(),
                    os.getEquipamento().getTipo() + " " + os.getEquipamento().getMarca() + " (" + os.getEquipamento().getModelo() + ")",
                    os.getDescricaoDefeito(),
                    fmtData.format(os.getDataLimite()),
                    dias < 0 ? "ATRASADA" : dias + " dia(s)"
            });
        }

        if (listaAtual.isEmpty()) {
            modeloTabela.addRow(new Object[] { "-", "Nenhuma OS aguardando técnico no momento", "", "", "", "" });
        }
    }

    private void iniciarAtendimento() {
        int linha = tabela.getSelectedRow();
        if (linha == -1 || listaAtual == null || linha >= listaAtual.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma OS na tabela primeiro!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrdemServico osSelecionada = listaAtual.get(linha);
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Assumir a OS #" + osSelecionada.getNumOs() + " (" + osSelecionada.getCliente().getNome() + ")?",
                "Iniciar Atendimento", JOptionPane.YES_NO_OPTION);

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        OrdemServico osAtualizada = osControler.assumirOS(osSelecionada.getNumOs(), tecnicoLogado);
        if (osAtualizada == null) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível assumir esta OS - outro técnico pode já tê-la pego. Atualize a lista.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            carregarDados();
            return;
        }

        // Independente do caminho escolhido, abre a tela padrão de detalhe/observações.
        new TelaDetalheOS(osAtualizada, tecnicoLogado).setVisible(true);
        dispose();
    }

    /** Pinta cada linha da tabela de acordo com a urgência da OS (RN03). */
    private class RendererDePrioridade extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (listaAtual != null && row < listaAtual.size()) {
                Color corFundo = Utils.corPrioridade(listaAtual.get(row));
                if (isSelected) {
                    c.setBackground(corFundo.darker());
                } else {
                    c.setBackground(corFundo);
                }
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    }
}

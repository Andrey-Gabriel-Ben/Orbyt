package com.senacsoluctions.view;

import java.awt.BorderLayout;
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
import javax.swing.table.DefaultTableModel;

import com.senacsoluctions.controler.OrdemServicoControler;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.Usuario;

/** T3 (opção 1): "listar ordens de serviço pendentes cujo técnico seja esse que está logado". */
public class TelaOSPendentesTecnico extends JFrame {

    private final Usuario tecnicoLogado;
    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<OrdemServico> listaAtual;

    public TelaOSPendentesTecnico(Usuario tecnico) {
        this.tecnicoLogado = tecnico;

        setTitle("Orbyt - Minhas OS Pendentes");
        setSize(720, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("OS atribuídas a " + tecnicoLogado.getNome() + " (em andamento)", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        add(lblTitulo, BorderLayout.NORTH);

        String[] colunas = { "Nº OS", "Cliente", "Equipamento", "Prazo Limite", "Situação" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnAbrir = new JButton("Abrir OS Selecionada");
        JButton btnAtualizar = new JButton("Atualizar Lista");
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnAbrir);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarDados());
        btnAbrir.addActionListener(e -> abrirSelecionada());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirSelecionada();
                }
            }
        });

        carregarDados();
    }

    private void carregarDados() {
        listaAtual = osControler.listarPendentesPorTecnico(tecnicoLogado);
        modeloTabela.setRowCount(0);

        for (OrdemServico os : listaAtual) {
            modeloTabela.addRow(new Object[] {
                    os.getNumOs(),
                    os.getCliente().getNome(),
                    os.getEquipamento().getTipo() + " " + os.getEquipamento().getMarca() + " (" + os.getEquipamento().getModelo() + ")",
                    fmtData.format(os.getDataLimite()),
                    Utils.descricaoPrioridade(os.getDataLimite())
            });
        }

        if (listaAtual.isEmpty()) {
            modeloTabela.addRow(new Object[] { "-", "Você não possui OS em andamento no momento", "", "", "" });
        }
    }

    private void abrirSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1 || listaAtual == null || linha >= listaAtual.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma OS na tabela primeiro!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrdemServico osSelecionada = listaAtual.get(linha);
        new TelaDetalheOS(osSelecionada, tecnicoLogado).setVisible(true);
    }
}

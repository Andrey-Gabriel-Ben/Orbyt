package com.senacsoluctions.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

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

/** T1: "lista as ordens de serviço finalizadas mas que ainda não foram retiradas (concluídas)". */
public class TelaOSFinalizadasPendentes extends JFrame {

    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnConfirmarEntrega;
    private JButton btnAtualizar;
    private List<OrdemServico> listaAtual;

    public TelaOSFinalizadasPendentes() {
        setTitle("Orbyt - OS Finalizadas (Entrega Pendente)");
        setSize(720, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("Equipamentos prontos aguardando retirada pelo cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 10, 12, 10));
        add(lblTitulo, BorderLayout.NORTH);

        String[] colunas = { "Nº OS", "Cliente", "Telefone", "Equipamento", "Data Abertura", "Custo (R$)" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        btnAtualizar = new JButton("Atualizar Lista");
        btnConfirmarEntrega = new JButton("Confirmar Entrega ao Cliente");
        btnConfirmarEntrega.setPreferredSize(new Dimension(220, 32));
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnConfirmarEntrega);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarDados());
        btnConfirmarEntrega.addActionListener(e -> confirmarEntrega());

        carregarDados();
    }

    private void carregarDados() {
        listaAtual = osControler.listarFinalizadasPendentesEntrega();
        modeloTabela.setRowCount(0);

        for (OrdemServico os : listaAtual) {
            modeloTabela.addRow(new Object[] {
                    os.getNumOs(),
                    os.getCliente().getNome(),
                    os.getCliente().getTelefone(),
                    os.getEquipamento().getTipo() + " " + os.getEquipamento().getMarca() + " (" + os.getEquipamento().getModelo() + ")",
                    fmtData.format(os.getDataAbertura()),
                    String.format("%.2f", os.getCusto())
            });
        }

        if (listaAtual.isEmpty()) {
            modeloTabela.addRow(new Object[] { "-", "Nenhuma OS finalizada aguardando retirada", "", "", "", "" });
        }
    }

    private void confirmarEntrega() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1 || listaAtual == null || linhaSelecionada >= listaAtual.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma OS na tabela primeiro!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrdemServico osSelecionada = listaAtual.get(linhaSelecionada);
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar a entrega do equipamento da OS #" + osSelecionada.getNumOs()
                        + " para " + osSelecionada.getCliente().getNome() + "?",
                "Confirmar Entrega", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            if (osControler.confirmarEntregaAoCliente(osSelecionada.getNumOs(), this)) {
                carregarDados();
            }
        }
    }
}

package com.senacsoluctions.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.text.MaskFormatter;
import javax.swing.table.DefaultTableModel;

import com.senacsoluctions.controler.OrdemServicoControler;
import com.senacsoluctions.dao.ClienteDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.OrdemServico;

/** T2: busca, por cliente (CPF), as OS que ainda não foram finalizadas nem concluídas. */
public class TelaBuscaOSPorCliente extends JFrame {

    private final ClienteDao clienteDao = new ClienteDao();
    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final SimpleDateFormat fmtData = new SimpleDateFormat("dd/MM/yyyy");

    private JFormattedTextField txtCpf;
    private JButton btnBuscar;
    private JLabel lblClienteInfo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaBuscaOSPorCliente() {
        setTitle("Orbyt - Buscar Ordens de Serviço por Cliente");
        setSize(740, 460);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelBusca = new JPanel(new GridBagLayout());
        painelBusca.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelBusca.add(new JLabel("CPF do Cliente:"), gbc);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter(' ');
            txtCpf = new JFormattedTextField(mascaraCpf);
        } catch (ParseException e) {
            txtCpf = new JFormattedTextField();
        }
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        painelBusca.add(txtCpf, gbc);

        btnBuscar = new JButton("Buscar");
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        painelBusca.add(btnBuscar, gbc);

        lblClienteInfo = new JLabel(" ");
        lblClienteInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        painelBusca.add(lblClienteInfo, gbc);

        add(painelBusca, BorderLayout.NORTH);

        String[] colunas = { "Nº OS", "Status", "Equipamento", "Data Abertura", "Prazo Limite", "Técnico" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscar());
    }

    private void buscar() {
        String cpf = txtCpf.getText();
        Cliente cliente = clienteDao.buscarPorCpf(cpf);
        modeloTabela.setRowCount(0);

        if (cliente == null) {
            lblClienteInfo.setText("Cliente não encontrado para o CPF informado.");
            lblClienteInfo.setForeground(new Color(255, 100, 100));
            return;
        }

        lblClienteInfo.setText("Cliente: " + cliente.getNome() + " | Tel: " + cliente.getTelefone());
        lblClienteInfo.setForeground(new Color(100, 200, 100));

        List<OrdemServico> lista = osControler.listarPendentesPorCliente(cliente);

        for (OrdemServico os : lista) {
            modeloTabela.addRow(new Object[] {
                    os.getNumOs(),
                    os.getStatus(),
                    os.getEquipamento().getTipo() + " " + os.getEquipamento().getMarca() + " (" + os.getEquipamento().getModelo() + ")",
                    fmtData.format(os.getDataAbertura()),
                    fmtData.format(os.getDataLimite()),
                    os.getTecnico() != null ? os.getTecnico().getNome() : "Não atribuído"
            });
        }

        if (lista.isEmpty()) {
            modeloTabela.addRow(new Object[] { "-", "Nenhuma OS em andamento para este cliente", "", "", "", "" });
        }
    }
}

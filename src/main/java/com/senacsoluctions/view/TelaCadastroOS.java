package com.senacsoluctions.view;

import com.senacsoluctions.controler.OrdemServicoControler;
import com.senacsoluctions.dao.ClienteDao;
import com.senacsoluctions.dao.EquipamentoDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaCadastroOS extends JFrame {

    private JFormattedTextField txtCpfCliente;
    private JButton btnBuscarCliente;
    private JLabel lblNomeClienteInfo;

    private JComboBox<Equipamento> cbEquipamento;
    private JButton btnNovoEquipamento;

    private JTextField txtCusto;
    private JTextArea txtDescricaoDefeito; // antes chamado (erroneamente) de txtObservacoes

    private JButton btnSalvarOS;
    private JLabel lblMensagemErro;

    private Cliente clienteSelecionado = null;
    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final ClienteDao clienteDao = new ClienteDao();

    public TelaCadastroOS() {
        setTitle("Abertura de Ordem de Serviço");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- BLOCO CLIENTE ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("CPF do Cliente:"), gbc);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter(' ');
            txtCpfCliente = new JFormattedTextField(mascaraCpf);
        } catch (Exception e) {
            txtCpfCliente = new JFormattedTextField();
        }

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(txtCpfCliente, gbc);

        btnBuscarCliente = new JButton("Buscar");
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0.0;
        add(btnBuscarCliente, gbc);

        lblNomeClienteInfo = new JLabel("Nenhum cliente selecionado");
        lblNomeClienteInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblNomeClienteInfo.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(lblNomeClienteInfo, gbc);

        // --- BLOCO EQUIPAMENTO ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Equipamento/Aparelho:"), gbc);

        cbEquipamento = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(cbEquipamento, gbc);

        btnNovoEquipamento = new JButton("+");
        btnNovoEquipamento.setToolTipText("Cadastrar novo equipamento para este cliente");
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.weightx = 0.0;
        add(btnNovoEquipamento, gbc);

        // --- BLOCO CUSTO ---
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Custo Estimado (R$):"), gbc);

        txtCusto = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(txtCusto, gbc);

        // --- BLOCO DESCRIÇÃO DO DEFEITO ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Descrição do Defeito:"), gbc);

        txtDescricaoDefeito = new JTextArea(4, 20);
        txtDescricaoDefeito.setLineWrap(true);
        txtDescricaoDefeito.setWrapStyleWord(true);
        JScrollPane scrollDefeito = new JScrollPane(txtDescricaoDefeito);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(scrollDefeito, gbc);

        // --- MENSAGEM DE ERRO ---
        lblMensagemErro = new JLabel("");
        lblMensagemErro.setForeground(new Color(255, 100, 100));
        lblMensagemErro.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        add(lblMensagemErro, gbc);

        // --- BOTÃO EMITIR OS ---
        btnSalvarOS = new JButton("Emitir Ordem de Serviço");
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 10, 10, 10);
        add(btnSalvarOS, gbc);

        // --- EVENTO: BUSCAR CLIENTE ---
        btnBuscarCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cpf = txtCpfCliente.getText();
                clienteSelecionado = clienteDao.buscarPorCpf(cpf);

                if (clienteSelecionado != null) {
                    lblNomeClienteInfo.setText("Cliente: " + clienteSelecionado.getNome());
                    lblNomeClienteInfo.setForeground(new Color(100, 255, 100));
                    lblMensagemErro.setText("");

                    // Atualiza a lista de aparelhos assim que o cliente é encontrado
                    atualizarListaEquipamentosDoCliente();
                } else {
                    lblNomeClienteInfo.setText("Cliente não encontrado!");
                    lblNomeClienteInfo.setForeground(new Color(255, 100, 100));
                    clienteSelecionado = null;
                    cbEquipamento.removeAllItems(); // Limpa se não achar cliente
                }
            }
        });

        // --- EVENTO: EMITIR OS ---
        btnSalvarOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                osControler.emitirOrdemServico(
                        clienteSelecionado,
                        cbEquipamento, // Enviando o JComboBox de equipamentos selecionado
                        txtCusto,
                        txtDescricaoDefeito,
                        lblMensagemErro,
                        TelaCadastroOS.this
                );
            }
        });

        // --- EVENTO: NOVO EQUIPAMENTO (+) ---
        btnNovoEquipamento.addActionListener(e -> {
            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Busque e selecione o cliente por CPF antes de adicionar um aparelho!",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TelaCadastroEquipamento dialog = new TelaCadastroEquipamento(this, clienteSelecionado);
            dialog.setVisible(true);

            if (dialog.isSalvoComSucesso()) {
                atualizarListaEquipamentosDoCliente();
            }
        });
    }

    private void atualizarListaEquipamentosDoCliente() {
        cbEquipamento.removeAllItems();
        if (clienteSelecionado != null) {
            EquipamentoDao eqDao = new EquipamentoDao();
            java.util.List<Equipamento> lista = eqDao.listarPorCliente(clienteSelecionado.getIdCliente());

            for (Equipamento eq : lista) {
                cbEquipamento.addItem(eq);
            }
        }
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new TelaCadastroOS().setVisible(true));
    }
}

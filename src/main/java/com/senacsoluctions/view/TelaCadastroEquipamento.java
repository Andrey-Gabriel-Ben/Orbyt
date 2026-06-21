package com.senacsoluctions.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.senacsoluctions.dao.EquipamentoDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;

public class TelaCadastroEquipamento extends JDialog {

    private JTextField txtTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumSerie;
    private JButton btnSalvar;
    private JLabel lblMensagemErro;

    private final Cliente clienteDono;
    private final EquipamentoDao equipDao = new EquipamentoDao();
    private boolean salvoComSucesso = false;

    // Construtor recebe a janela pai e o cliente selecionado
    public TelaCadastroEquipamento(Frame pai, Cliente cliente) {
        super(pai, "Cadastrar Equipamento do Cliente", true); // true ativa o MODAL
        this.clienteDono = cliente;

        setSize(400, 320);
        setLocationRelativeTo(pai);
        setResizable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mostrar o dono do equipamento no topo para orientação
        JLabel lblDono = new JLabel("Dono: " + cliente.getNome());
        lblDono.setFont(new Font("Arial", Font.BOLD, 12));
        lblDono.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblDono, gbc);

        // Campos do Formulário
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Tipo (Ex: Celular):"), gbc);
        txtTipo = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        add(txtTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        add(new JLabel("Marca (Ex: Samsung):"), gbc);
        txtMarca = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        add(txtMarca, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        add(new JLabel("Modelo (Ex: Galaxy S23):"), gbc);
        txtModelo = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        add(txtModelo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        add(new JLabel("Nº de Série (Opcional):"), gbc);
        txtNumSerie = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        add(txtNumSerie, gbc);

        // Feedback de Erro
        lblMensagemErro = new JLabel("");
        lblMensagemErro.setForeground(new Color(255, 100, 100));
        lblMensagemErro.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(lblMensagemErro, gbc);

        // Botão Salvar
        btnSalvar = new JButton("Salvar Equipamento");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 12, 12, 12);
        add(btnSalvar, gbc);

        // Ação do Botão
        btnSalvar.addActionListener(e -> efetuarCadastro());
    }

    private void efetuarCadastro() {
        // Limpa bordas vermelhas antigas
        txtTipo.putClientProperty("JComponent.outline", null);
        txtMarca.putClientProperty("JComponent.outline", null);
        txtModelo.putClientProperty("JComponent.outline", null);
        lblMensagemErro.setText("");

        // Validações Obrigatórias
        if (txtTipo.getText().trim().isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtTipo, "O tipo do equipamento é obrigatório!");
            return;
        }
        if (txtMarca.getText().trim().isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtMarca, "A marca do equipamento é obrigatória!");
            return;
        }
        if (txtModelo.getText().trim().isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtModelo, "O modelo do equipamento é obrigatório!");
            return;
        }

        // Instancia o modelo preenchendo as informações
        Equipamento novoEquip = new Equipamento();
        novoEquip.setTipo(txtTipo.getText().trim());
        novoEquip.setMarca(txtMarca.getText().trim());
        novoEquip.setModelo(txtModelo.getText().trim());
        novoEquip.setNumSerie(txtNumSerie.getText().trim());
        novoEquip.setCliente(clienteDono); // Associa a FK ao Cliente Dono

        // Grava no Supabase via DAO
        boolean sucesso = equipDao.salvar(novoEquip);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Equipamento vinculado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            this.salvoComSucesso = true;
            dispose(); // Fecha o dialog
        } else {
            lblMensagemErro.setText("Erro de conexão ao salvar equipamento no banco.");
        }
    }

    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }
}
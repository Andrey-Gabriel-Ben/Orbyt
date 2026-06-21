package com.senacsoluctions.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

import com.senacsoluctions.controler.ClienteControler;

public class TelaCadastroCliente extends JFrame {

    // Componentes da tela (Declaração)
    private JLabel lblTitulo;
    private JLabel lblNome, lblCpf, lblTelefone, lblEmail;
    private JTextField txtNome, txtEmail;
    private JFormattedTextField txtCpf, txtTelefone; // Campos com máscara
    private JButton btnSalvar;
    private JLabel lblMensagemErro;

    // Instância do controlador de clientes
    private ClienteControler clienteControler = new ClienteControler();

    public TelaCadastroCliente() {
        // 1. Configurações básicas da Janela (Janela secundária)
        setTitle("Orbyt - Cadastro de Cliente");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE impede de fechar o sistema inteiro
        setLocationRelativeTo(null); // Centraliza a tela
        setResizable(false);

        // 2. Definindo o gerenciador de Layout (GridBagLayout)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12); // Margens entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 3. Inicializando os Componentes
        lblTitulo = new JLabel("Novo Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        lblNome = new JLabel("Nome Completo *:");
        lblCpf = new JLabel("CPF *:");
        lblTelefone = new JLabel("Telefone *:");
        lblEmail = new JLabel("E-mail:");

        txtNome = new JTextField();
        txtNome.setPreferredSize(new Dimension(220, 30));

        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(220, 30));

        // Aplicação das Máscaras nos campos de CPF e Telefone
        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter(' '); // Define espaços vazios na máscara
            txtCpf = new JFormattedTextField(mascaraCpf);
            txtCpf.setPreferredSize(new Dimension(220, 30));

            // Máscara de telefone celular padrão brasileiro de 9 dígitos: (##) #####-####
            MaskFormatter mascaraTel = new MaskFormatter("(##) #####-####");
            mascaraTel.setPlaceholderCharacter(' ');
            txtTelefone = new JFormattedTextField(mascaraTel);
            txtTelefone.setPreferredSize(new Dimension(220, 30));

        } catch (ParseException e) {
            System.err.println("Erro ao criar máscaras da tela de clientes: " + e.getMessage());
            // Fallback caso a máscara falhe por algum motivo de biblioteca
            txtCpf = new JFormattedTextField();
            txtTelefone = new JFormattedTextField();
        }

        btnSalvar = new JButton("Salvar Cadastro");
        btnSalvar.setPreferredSize(new Dimension(200, 38));

        lblMensagemErro = new JLabel("", SwingConstants.CENTER);
        lblMensagemErro.setForeground(new Color(255, 100, 100)); // Destaca o erro no FlatDarkLaf
        lblMensagemErro.setFont(new Font("Arial", Font.BOLD, 12));

        // 4. Posicionamento dos Componentes no GridBagLayout
        // Linha 0: Título Principal (Ocupa as 2 colunas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 12, 20, 12); // Mais espaço abaixo do título
        add(lblTitulo, gbc);

        // Resetando largura padrão para as linhas de formulário comuns
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 12, 6, 12);

        // Linha 1: Nome
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblNome, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(txtNome, gbc);

        // Linha 2: CPF
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblCpf, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        add(txtCpf, gbc);

        // Linha 3: Telefone
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblTelefone, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        add(txtTelefone, gbc);

        // Linha 4: E-mail
        gbc.gridx = 0; gbc.gridy = 4;
        add(lblEmail, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        add(txtEmail, gbc);

        // Linha 6: Label de Mensagem de Erro (Ocupa 2 colunas)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 12, 6, 12);
        add(lblMensagemErro, gbc);

        // Linha 7: Botão Salvar (Ocupa 2 colunas)
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 12, 15, 12);
        add(btnSalvar, gbc);

        // 5. Configuração de Eventos
        configurarEventos();
    }

    private void configurarEventos() {
        // Ação disparada ao clicar no botão de salvar
        btnSalvar.addActionListener(e -> {
            // Delega diretamente para a regra de negócio do controlador
            clienteControler.cadastrarCliente(
                txtNome, 
                txtCpf, 
                txtTelefone, 
                txtEmail, 
                lblMensagemErro, 
                this
            );
        });
    }

    // Método main para executar e testar este layout de forma isolada
    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Erro ao carregar tema FlatLaf: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new TelaCadastroCliente().setVisible(true);
        });
    }
}
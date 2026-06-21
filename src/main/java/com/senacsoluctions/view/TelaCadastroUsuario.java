package com.senacsoluctions.view;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf; // Caso queira testar a janela isolada
import com.senacsoluctions.controler.UsuarioControler;

public class TelaCadastroUsuario extends JFrame {

    private UsuarioControler uc = new UsuarioControler();

    // Componentes da Interface (Labels e Campos)
    private JLabel lblTitulo;
    private JLabel lblNome;
    private JLabel lblLogin;
    private JLabel lblSenha;
    private JLabel lblCargo;

    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<String> cbCargo; // Caixa de seleção para evitar erros de digitação

    private JButton btnSalvar;
    private JButton btnCancelar;
    private JLabel lblMensagemErro; // Label para validações visuais

    public TelaCadastroUsuario() {
        // 1. Configurações básicas da janela de cadastro
        setTitle("Orbyt - Cadastro de Usuário");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não o sistema todo
        setLocationRelativeTo(null);
        setResizable(false);

        // 2. Configurando o layout principal (GridBagLayout)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15); // Espaçamento interno padrão
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITULO DA TELA ---
        lblTitulo = new JLabel("Novo Usuário", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(173, 216, 230)); // Destaque em azul claro

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa as duas colunas do formulário
        gbc.insets = new Insets(15, 15, 20, 15); // Mais espaço abaixo do título
        add(lblTitulo, gbc);

        // Resetando o espaçamento para os campos do formulário
        gbc.insets = new Insets(6, 15, 6, 15);
        gbc.gridwidth = 1;

        // --- CAMPO: NOME ---
        lblNome = new JLabel("Nome Completo:");
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblNome, gbc);

        txtNome = new JTextField();
        txtNome.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtNome, gbc);

        // --- CAMPO: LOGIN ---
        lblLogin = new JLabel("Usuário (Login):");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblLogin, gbc);

        txtLogin = new JTextField();
        txtLogin.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtLogin, gbc);

        // --- CAMPO: SENHA ---
        lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(txtSenha, gbc);

        // --- CAMPO: CARGO ---
        lblCargo = new JLabel("Cargo / Função:");
        lblCargo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblCargo, gbc);

        // Opções travadas de acordo com as regras de negócio mapeadas
        String[] cargos = { "ATENDENTE", "TECNICO" };
        cbCargo = new JComboBox<>(cargos);
        cbCargo.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(cbCargo, gbc);

        // --- LABEL DE MENSAGEM DE ERRO ---
        lblMensagemErro = new JLabel("", SwingConstants.CENTER);
        lblMensagemErro.setForeground(new Color(255, 100, 100)); // Vermelho suave do FlatLaf
        lblMensagemErro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 10, 15);
        add(lblMensagemErro, gbc);

        // --- PAINEL DE BOTÕES (Lado a Lado) ---
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 15, 0));
        btnCancelar = new JButton("Cancelar");
        btnSalvar = new JButton("Salvar Cadastro");

        // Aplica o estilo "Destrutivo" (Fundo avermelhado suave que combina com o Dark
        // Mode)
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.setBackground(new Color(180, 50, 50));
        btnCancelar.setForeground(Color.WHITE);

        // Aplica o estilo "Principal/Ação" (Fundo azul moderno)
        btnSalvar.putClientProperty("JButton.buttonType", "roundRect");
        btnSalvar.setBackground(new Color(30, 144, 255)); // Azul Dodger
        btnSalvar.setForeground(Color.WHITE);

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 15, 15);
        add(painelBotoes, gbc);

        // 3. Configurando as ações básicas dos botões
        configurarEventos();
    }

    private void configurarEventos() {
        // Botão Cancelar apenas fecha a tela de cadastro atual
        btnCancelar.addActionListener(e -> dispose());

        // Botão Salvar dispara as validações e futuramente a persistência
        btnSalvar.addActionListener(e -> {
            uc.cadastrarNovoUsuario(txtNome, txtLogin, txtSenha, cbCargo, lblMensagemErro, this);
        });
    }

    // Método Main opcional para testar o design visual da tela de forma
    // independente
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new TelaCadastroUsuario().setVisible(true);
        });
    }
}
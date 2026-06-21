package com.senacsoluctions.view;

import javax.swing.*;

import com.senacsoluctions.model.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.senacsoluctions.controler.UsuarioControler;

public class TelaLogin extends JFrame {

    // Componentes da tela (Declaração)
    private JLabel lblLogo;
    private JLabel lblLogin;
    private JLabel lblSenha;
    private JTextField txtLogin;
    private JPasswordField txtSenha; // JPasswordField esconde os caracteres com bolinhas
    private JButton btnLogin;
    private JLabel lblMensagemErro; // Label para mostrar o texto em vermelho

    public TelaLogin() {
        // 1. Configurações básicas da Janela
        setTitle("Orbyt - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 2. Definindo o gerenciador de Layout (GridBagLayout)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margem/Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Faz os campos esticarem na horizontal

        // 3. Criando os componentes

        // imagem da logo
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo-app.png"));
            lblLogo = new JLabel(icon);
        } catch (Exception e) {
            lblLogo = new JLabel("SENAC SOLUTIONS", SwingConstants.CENTER);
            lblLogo.setFont(new Font("Arial", Font.BOLD, 24)); // Texto grande e em negrito
        }

        lblLogin = new JLabel("Login:");
        lblSenha = new JLabel("Senha:");
        txtLogin = new JTextField(15); // Tamanho sugerido do campo
        txtSenha = new JPasswordField(15);
        btnLogin = new JButton("Entrar");

        // Estilizando o botão de leve para ficar mais moderno
        // Um verde mais elegante que combina perfeitamente com fundos escuros
        btnLogin.setBackground(new Color(46, 139, 87));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));

        // 4. Posicionando os componentes na "Grade" (X = Coluna, Y = Linha)

        // Linha 0: Logo (Centralizada ocupando duas colunas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa a coluna 0 e 1
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblLogo, gbc);

        // Resetando a largura padrão para os próximos componentes
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 1: Label do Login
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblLogin, gbc);

        // Linha 1: Campo de texto do Login
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtLogin, gbc);

        // Linha 2: Label da Senha
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblSenha, gbc);

        // Linha 2: Campo de texto da Senha
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(txtSenha, gbc);

        // Criando o label de erro (inicia vazio)
        lblMensagemErro = new JLabel("");
        lblMensagemErro.setForeground(new Color(255, 100, 100)); // Vermelho claro que destaca no tema escuro
        lblMensagemErro.setFont(new Font("Arial", Font.BOLD, 12));

        // Adicionando na grade (Linha 3, ocupando as duas colunas)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(lblMensagemErro, gbc);

        // Linha 3: Botão de Login (Ocupando as duas colunas embaixo)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); // Mais espaçamento em cima do botão
        add(btnLogin, gbc);

        // 5. Evento do Botão (O que acontece quando clica)
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Agora passa o txtSenha (que é JPasswordField) perfeitamente
                UsuarioControler.efetuarLogin(txtLogin, txtSenha, lblMensagemErro);
            }
        });
    }

    // Método Main provisório dentro da View para você testar a janela isolada
    public static void main(String[] args) {
        // 1. Ativa o tema escuro moderno para TODOS os componentes do sistema
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Não foi possível iniciar o tema FlatLaf. Usando padrão.");
        }

        // 2. Garante que a interface rode na thread correta de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}
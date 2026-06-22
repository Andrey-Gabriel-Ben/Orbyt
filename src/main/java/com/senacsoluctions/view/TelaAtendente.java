package com.senacsoluctions.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.senacsoluctions.controler.UsuarioControler;
import com.senacsoluctions.model.Usuario;

public class TelaAtendente extends JFrame {
    UsuarioControler uc = new UsuarioControler();
    private Usuario atendenteLogado;

    // Componentes da Interface
    private JLabel lblBoasVindas;
    private JButton btnCadastrarCliente;
    private JButton btnCadastrarUsuario;
    private JButton btnNovaOS;
    private JButton btnOSPendentesEntrega;
    private JButton btnBuscarOS;

    public TelaAtendente(Usuario usuario) {
        this.atendenteLogado = usuario;

        // Configurações básicas da janela
        setTitle("Orbyt - Painel do Atendente");
        setSize(550, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gerenciador de Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Mensagem de Boas-vindas no topo
        lblBoasVindas = new JLabel("Olá, " + atendenteLogado.getNome() + " | Painel de Atendimento",
                SwingConstants.CENTER);
        lblBoasVindas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBoasVindas.setForeground(new Color(173, 216, 230));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 20, 25, 20);
        add(lblBoasVindas, gbc);

        gbc.insets = new Insets(8, 20, 8, 20);

        // 2. Inicializando os Botões
        btnCadastrarCliente = new JButton("Cadastrar Clientes");
        btnCadastrarUsuario = new JButton("Cadastrar Usuários (Funcionários)");
        btnNovaOS = new JButton("Iniciar Nova Ordem de Serviço");
        btnOSPendentesEntrega = new JButton("OS Finalizadas (Entrega Pendente)");
        btnBuscarOS = new JButton("Buscar Ordens de Serviço");

        Font fonteBotoes = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension tamanhoBotao = new Dimension(220, 42);

        JButton[] botoes = { btnCadastrarCliente, btnCadastrarUsuario, btnNovaOS, btnOSPendentesEntrega, btnBuscarOS };
        for (JButton btn : botoes) {
            btn.setFont(fonteBotoes);
            btn.setPreferredSize(tamanhoBotao);
        }

        // 3. Posicionando os botões na tela
        gbc.gridy = 1;
        add(btnCadastrarCliente, gbc);

        gbc.gridy = 2;
        add(btnCadastrarUsuario, gbc);

        gbc.gridy = 3;
        add(btnNovaOS, gbc);

        gbc.gridy = 4;
        add(btnOSPendentesEntrega, gbc);

        gbc.gridy = 5;
        add(btnBuscarOS, gbc);

        // 4. Configuração das Ações dos Botões (Eventos)
        configurarEventos();
    }

    private void configurarEventos() {
        btnCadastrarCliente.addActionListener(e -> {
            new TelaCadastroCliente().setVisible(true);
        });

        btnCadastrarUsuario.addActionListener(e -> {
            new TelaCadastroUsuario().setVisible(true);
        });

        btnNovaOS.addActionListener(e -> {
            new TelaCadastroOS().setVisible(true);
        });

        // T1: lista as OS já finalizadas pelo técnico que ainda não foram retiradas
        btnOSPendentesEntrega.addActionListener(e -> {
            new TelaOSFinalizadasPendentes().setVisible(true);
        });

        // T2: busca, por cliente, as OS que ainda não foram finalizadas nem concluídas
        btnBuscarOS.addActionListener(e -> {
            new TelaBuscaOSPorCliente().setVisible(true);
        });
    }

}

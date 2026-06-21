package com.senacsoluctions.view;

import javax.swing.*;
import java.awt.*;
import com.senacsoluctions.model.Usuario;

public class TelaAtendente extends JFrame {

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

        // Configurações básicas da janela do Menu Principal
        setTitle("Orbyt - Painel do Atendente");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gerenciador de Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Espaçamento harmônico
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Mensagem de Boas-vindas no topo
        lblBoasVindas = new JLabel("Olá, " + atendenteLogado.getNome() + " | Atendimento", SwingConstants.CENTER);
        lblBoasVindas.setFont(new Font("Arial", Font.BOLD, 18));
        lblBoasVindas.setForeground(new Color(173, 216, 230)); // Um azul claro elegante para destacar

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(lblBoasVindas, gbc);

        // 2. Inicializando os Botões solicitados
        btnCadastrarCliente = new JButton("👥 Cadastrar Clientes");
        btnCadastrarUsuario = new JButton("🔑 Cadastrar Usuários (Funcionários)");
        btnNovaOS = new JButton("➕ Iniciar Nova Ordem de Serviço");
        btnOSPendentesEntrega = new JButton("📦 OS Finalizadas (Entrega Pendente)");
        btnBuscarOS = new JButton("🔍 Buscar OS (Nome / CPF)");

        // Estilizando os botões para dar uma identidade visual moderna (FlatLaf se encarrega do resto)
        Font fonteBotoes = new Font("Arial", Font.PLAIN, 14);
        Dimension tamanhoBotao = new Dimension(200, 40);

        JButton[] botoes = {btnCadastrarCliente, btnCadastrarUsuario, btnNovaOS, btnOSPendentesEntrega, btnBuscarOS};
        for (JButton btn : botoes) {
            btn.setFont(fonteBotoes);
            btn.setPreferredSize(tamanhoBotao);
        }

        // Destacando o botão de Nova OS por usabilidade (cor azul de ação)
        btnNovaOS.setBackground(new Color(30, 144, 255));
        btnNovaOS.setForeground(Color.WHITE);

        // 3. Posicionando os botões na tela (Um embaixo do outro)
        
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
            JOptionPane.showMessageDialog(this, "Abrindo cadastro de clientes...");
            // Exemplo futuro: new TelaCadastroCliente().setVisible(true);
        });

        btnCadastrarUsuario.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrindo cadastro de usuários...");
        });

        btnNovaOS.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrindo abertura de nova OS...");
        });

        btnOSPendentesEntrega.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Buscando OS prontas no Supabase...");
        });

        btnBuscarOS.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Abrindo busca avançada por Nome/CPF...");
        });
    }
}
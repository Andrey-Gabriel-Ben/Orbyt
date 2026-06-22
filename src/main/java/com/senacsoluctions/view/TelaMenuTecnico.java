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

import com.senacsoluctions.model.Usuario;

/**
 * T3: tela exibida ao técnico logo após o login, para ele escolher entre
 * ver as OS que já assumiu ("Minhas OS Pendentes") ou pegar uma nova OS
 * da fila ("Iniciar Nova OS").
 */
public class TelaMenuTecnico extends JFrame {

    private final Usuario tecnicoLogado;

    private JLabel lblBoasVindas;
    private JButton btnMinhasOSPendentes;
    private JButton btnIniciarNovaOS;

    public TelaMenuTecnico(Usuario tecnico) {
        this.tecnicoLogado = tecnico;

        setTitle("Orbyt - Painel do Técnico");
        setSize(480, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblBoasVindas = new JLabel("Olá, " + tecnicoLogado.getNome() + " | O que deseja fazer?",
                SwingConstants.CENTER);
        lblBoasVindas.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBoasVindas.setForeground(new Color(173, 216, 230));
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblBoasVindas, gbc);

        btnMinhasOSPendentes = new JButton("Listar Minhas OS Pendentes");
        btnIniciarNovaOS = new JButton("Iniciar Nova OS (Fila de Atendimento)");

        Font fonteBotoes = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension tamanhoBotao = new Dimension(260, 44);
        btnMinhasOSPendentes.setFont(fonteBotoes);
        btnMinhasOSPendentes.setPreferredSize(tamanhoBotao);
        btnIniciarNovaOS.setFont(fonteBotoes);
        btnIniciarNovaOS.setPreferredSize(tamanhoBotao);

        gbc.gridy = 1;
        add(btnMinhasOSPendentes, gbc);

        gbc.gridy = 2;
        add(btnIniciarNovaOS, gbc);

        btnMinhasOSPendentes.addActionListener(e -> {
            new TelaOSPendentesTecnico(tecnicoLogado).setVisible(true);
        });

        btnIniciarNovaOS.addActionListener(e -> {
            new TelaIniciarOS(tecnicoLogado).setVisible(true);
        });
    }
}

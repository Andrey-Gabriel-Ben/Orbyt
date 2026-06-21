package com.senacsoluctions.controler;

import com.senacsoluctions.model.Usuario;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JPasswordField;

import com.senacsoluctions.dao.UsuarioDao;

//import com.senacsoluctions.dao.UsuarioDAO; 

public class UsuarioControler {

    public static void efetuarLogin(JTextField txtLogin, JPasswordField txtSenha, JLabel lblMensagemErro) {
        String login = txtLogin.getText().trim();

        // Agora o .getPassword() vai funcionar perfeitamente!
        String senha = new String(txtSenha.getPassword()).trim();

        // Limpa os estados de erro anteriores antes de validar de novo
        txtLogin.putClientProperty("JComponent.outline", null);
        txtSenha.putClientProperty("JComponent.outline", null);
        lblMensagemErro.setText("");

        // Valida o campo de Login
        if (login.isEmpty()) {
            lblMensagemErro.setText("Preencha o campo de usuário!");
            txtLogin.putClientProperty("JComponent.outline", "error");
            txtLogin.requestFocus();
            return;
        }

        // Valida o campo de Senha
        if (senha.isEmpty()) {
            lblMensagemErro.setText("Preencha o campo de senha!");
            txtSenha.putClientProperty("JComponent.outline", "error");
            txtSenha.requestFocus();
            return;
        }

        // 3. LOGICA DO BANCO CONECTADA
        UsuarioDao usuarioDao = new UsuarioDao();
        Usuario usuarioLogado = usuarioDao.autenticarUsuario(login, senha);

        if (usuarioLogado != null) {
            lblMensagemErro.setText("");

            // 1. Fecha a Tela de Login que está aberta no momento
            java.awt.Window janelaLogin = SwingUtilities.getWindowAncestor(txtLogin);
            if (janelaLogin != null) {
                janelaLogin.dispose();
            }

            // 2. Valida o cargo do usuário e decide qual tela abrir
            String cargo = usuarioLogado.getCargo().toUpperCase().trim();

            switch (cargo) {
                case "ATENDENTE" -> SwingUtilities.invokeLater(() -> {
                    new TelaAtendente(usuarioLogado).setVisible(true);
                });

                case "TECNICO" -> {
                    JOptionPane.showMessageDialog(null, "Tela do Técnico em desenvolvimento!");
                    // new TelaTecnico(usuarioLogado).setVisible(true);
                }

                default -> {
                    // Se cair aqui, é porque tem um cargo no banco que não mapeamos no código
                    JOptionPane.showMessageDialog(null, "Cargo não reconhecido pelo sistema.", "Erro de Permissão",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Se errar no banco, avisa o usuário e acende os dois em vermelho
            lblMensagemErro.setText("Usuário ou senha incorretos.");
            txtLogin.putClientProperty("JComponent.outline", "error");
            txtSenha.putClientProperty("JComponent.outline", "error");
        }

    }
}
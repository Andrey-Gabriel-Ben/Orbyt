package com.senacsoluctions.controler;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.mindrot.jbcrypt.BCrypt;

import com.senacsoluctions.dao.UsuarioDao;
import com.senacsoluctions.model.Usuario;
import com.senacsoluctions.view.TelaAtendente;
import com.senacsoluctions.view.TelaMenuTecnico;



public class UsuarioControler {
    UsuarioDao ud = new UsuarioDao();

    public void efetuarLogin(JTextField txtLogin, JPasswordField txtSenha, JLabel lblMensagemErro) {
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
        Usuario usuarioLogado = ud.autenticarUsuario(login, senha);

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

                // T3: técnico agora vai para a tela de escolha de função
                // (Minhas OS Pendentes / Iniciar Nova OS) em vez do diálogo provisório.
                case "TECNICO" -> SwingUtilities.invokeLater(() -> {
                    new TelaMenuTecnico(usuarioLogado).setVisible(true);
                });

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

    public static void cadastrarNovoUsuario(JTextField txtNome, JTextField txtLogin, JPasswordField txtSenha,
            JComboBox<String> cbCargo, JLabel lblMensagemErro, JFrame telaAtual) {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        String cargo = (String) cbCargo.getSelectedItem();

        // Limpando estados de erro visuais anteriores
        txtNome.putClientProperty("JComponent.outline", null);
        txtLogin.putClientProperty("JComponent.outline", null);
        txtSenha.putClientProperty("JComponent.outline", null);
        lblMensagemErro.setText("");

        // Validações de campos vazios integradas ao FlatLaf
        if (nome.isEmpty()) {
            lblMensagemErro.setText("O campo Nome é obrigatório!");
            txtNome.putClientProperty("JComponent.outline", "error");
            txtNome.requestFocus();
            return;
        }

        if (login.isEmpty()) {
            lblMensagemErro.setText("O campo Usuário (Login) é obrigatório!");
            txtLogin.putClientProperty("JComponent.outline", "error");
            txtLogin.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            lblMensagemErro.setText("O campo Senha é obrigatório!");
            txtSenha.putClientProperty("JComponent.outline", "error");
            txtSenha.requestFocus();
            return;
        }

        if (senha.length() < 8) {
            lblMensagemErro.setText("A Senha precisa ter mais de 8 dígitos!");
            txtSenha.putClientProperty("JComponent.outline", "error");
            txtSenha.requestFocus();
            return;
        }

        // --- SE PASSOU NAS VALIDAÇÕES: CRIPTOGRAFIA E PERSISTÊNCIA ---

       // 1. Gera o Hash seguro da senha usando BCrypt
        String senhaHasheada = BCrypt.hashpw(senha, BCrypt.gensalt());

        // 2. Monta o Objeto de Modelo (User)
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setLogin(login);
        novoUsuario.setSenha(senhaHasheada);
        novoUsuario.setCargo(cargo);

        // 3. Integração REAL com o banco através do DAO criado

        // Primeiro, valida se o login já existe para evitar erro de constraint duplicada
        // (agora que UsuarioDao.buscarPorLogin foi corrigido, essa checagem funciona de verdade)
        UsuarioDao usuarioDao = new UsuarioDao();
        if (usuarioDao.buscarPorLogin(login) != null) {
            lblMensagemErro.setText("Este nome de usuário (login) já está em uso!");
            txtLogin.putClientProperty("JComponent.outline", "error");
            txtLogin.requestFocus();
            return;
        }

        // Chama o método do DAO para persistir
        boolean sucesso = usuarioDao.salvarUsuario(novoUsuario);

        if (sucesso) {
            JOptionPane.showMessageDialog(telaAtual, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            telaAtual.dispose(); // Fecha a tela de cadastro automaticamente ao finalizar
        } else {
            lblMensagemErro.setText("Erro de conexão ao salvar no banco. Tente novamente.");
        }

    }

}

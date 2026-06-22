package com.senacsoluctions.controler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.senacsoluctions.BaseTest;
import com.senacsoluctions.dao.UsuarioDao;
import com.senacsoluctions.model.Usuario;
import com.senacsoluctions.view.TelaAtendente;
import com.senacsoluctions.view.TelaMenuTecnico;

/**
 * Testes unitários do módulo de Usuários e Autenticação.
 * CT-U01 a CT-U07 — cobertura de login válido/inválido, cadastro com regras
 * de senha mínima, login duplicado e roteamento de perfil.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo de Usuários e Autenticação")
class UsuarioControlerTest extends BaseTest {

    // Componentes Swing para login
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JLabel lblErroLogin;
    private UsuarioControler uc;

    // Componentes Swing para cadastro
    private JTextField txtNomeCad;
    private JTextField txtLoginCad;
    private JPasswordField txtSenhaCad;
    private JComboBox<String> cbCargo;
    private JLabel lblErroCad;
    private JFrame mockFrame;

    @BeforeEach
    void setUp() {
        uc = new UsuarioControler();

        // Login
        txtLogin     = new JTextField();
        txtSenha     = new JPasswordField();
        lblErroLogin = new JLabel();

        // Cadastro
        txtNomeCad  = new JTextField();
        txtLoginCad = new JTextField();
        txtSenhaCad = new JPasswordField();
        cbCargo     = new JComboBox<>(new String[]{"ATENDENTE", "TECNICO"});
        lblErroCad  = new JLabel();
        mockFrame   = mock(JFrame.class);
    }

    // -----------------------------------------------------------------------
    // CT-U01: Login com credenciais válidas
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U01 - Login com credenciais válidas deve autenticar e abrir tela correta")
    void ctU01_loginComCredenciaisValidas() {

        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(1);
        usuarioMock.setNome("João Atendente");
        usuarioMock.setLogin("joao");
        usuarioMock.setCargo("ATENDENTE");

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> when(mock.autenticarUsuario(eq("joao"), eq("senha123")))
                        .thenReturn(usuarioMock));
             MockedConstruction<TelaAtendente> mockedTela = mockConstruction(TelaAtendente.class)) {

            txtLogin.setText("joao");
            txtSenha.setText("senha123");

            uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);

            // Aguarda execução do SwingUtilities.invokeLater
            aguardarEDT();

            assertEquals("", lblErroLogin.getText(), "Não deve haver mensagem de erro no login válido");
            assertFalse(mockedTela.constructed().isEmpty(),
                    "TelaAtendente deve ter sido instanciada após login de ATENDENTE válido");
        }
    }

    // -----------------------------------------------------------------------
    // CT-U02: Bloquear login com senha incorreta
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U02 - Senha incorreta deve exibir mensagem de erro")
    void ctU02_bloquearLoginComSenhaIncorreta() {

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> when(mock.autenticarUsuario(anyString(), anyString()))
                        .thenReturn(null))) { // DAO retorna null → credenciais inválidas

            txtLogin.setText("joao");
            txtSenha.setText("senhaErrada");

            uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);

            assertFalse(lblErroLogin.getText().isEmpty(),
                    "Deve haver mensagem de erro para senha incorreta");
            assertTrue(lblErroLogin.getText().toLowerCase().contains("incorretos") ||
                       lblErroLogin.getText().toLowerCase().contains("inválido") ||
                       lblErroLogin.getText().toLowerCase().contains("senha"),
                    "Mensagem deve indicar credenciais inválidas");
        }
    }

    @Test
    @DisplayName("CT-U02b - Login vazio deve ser bloqueado antes de consultar o banco")
    void ctU02b_loginVazioDeveSerBloqueado() {
        txtLogin.setText("");
        txtSenha.setText("senha123");

        uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);

        assertFalse(lblErroLogin.getText().isEmpty(), "Campo login vazio deve gerar mensagem de erro");
    }

    @Test
    @DisplayName("CT-U02c - Senha vazia deve ser bloqueada antes de consultar o banco")
    void ctU02c_senhaVaziaDeveSerBloqueada() {
        txtLogin.setText("joao");
        txtSenha.setText("");

        uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);

        assertFalse(lblErroLogin.getText().isEmpty(), "Campo senha vazio deve gerar mensagem de erro");
    }

    // -----------------------------------------------------------------------
    // CT-U03: Cadastrar usuário com login único e senha >= 8 chars
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U03 - Usuário com login único e senha >= 8 deve ser salvo com BCrypt")
    void ctU03_cadastrarUsuarioComLoginUnicoESenhaValida() {

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> {
                    when(mock.buscarPorLogin(anyString())).thenReturn(null); // login disponível
                    when(mock.salvarUsuario(any(Usuario.class))).thenReturn(true);
                });
             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {

            preencherCadastroValido();

            UsuarioControler.cadastrarNovoUsuario(txtNomeCad, txtLoginCad, txtSenhaCad,
                    cbCargo, lblErroCad, mockFrame);

            assertEquals("", lblErroCad.getText(), "Não deve haver erro para cadastro válido");

            // Verifica que o DAO foi chamado com o objeto de usuário
            UsuarioDao daoInstanciado = mockedDao.constructed().get(0);
            org.mockito.ArgumentCaptor<Usuario> captor =
                    org.mockito.ArgumentCaptor.forClass(Usuario.class);
            verify(daoInstanciado).salvarUsuario(captor.capture());

            Usuario usuarioSalvo = captor.getValue();
            // Senha deve ser o hash BCrypt, não a senha em texto puro
            assertNotEquals("Senha@2024", usuarioSalvo.getSenha(),
                    "A senha salva no banco deve ser o hash BCrypt, não o texto puro");
            assertTrue(usuarioSalvo.getSenha().startsWith("$2a$"),
                    "Hash BCrypt deve começar com $2a$");
        }
    }

    // -----------------------------------------------------------------------
    // CT-U04: Bloquear login duplicado no cadastro
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U04 - Login já existente deve ser rejeitado no cadastro")
    void ctU04_bloquearLoginDuplicado() {

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setLogin("tecnico01");

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> when(mock.buscarPorLogin(eq("tecnico01")))
                        .thenReturn(usuarioExistente))) {

            preencherCadastroValido();
            txtLoginCad.setText("tecnico01"); // login já em uso

            UsuarioControler.cadastrarNovoUsuario(txtNomeCad, txtLoginCad, txtSenhaCad,
                    cbCargo, lblErroCad, mockFrame);

            assertFalse(lblErroCad.getText().isEmpty(),
                    "Deve haver mensagem de erro para login duplicado");

            // Garante que salvarUsuario NÃO foi chamado
            UsuarioDao daoInstanciado = mockedDao.constructed().get(0);
            verify(daoInstanciado, never()).salvarUsuario(any());
        }
    }

    // -----------------------------------------------------------------------
    // CT-U05: Bloquear senha com menos de 8 caracteres
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U05 - Senha com menos de 8 caracteres deve ser rejeitada")
    void ctU05_bloquearSenhaCurta() {

        txtNomeCad.setText("Pedro Costa");
        txtLoginCad.setText("pedro");
        txtSenhaCad.setText("1234567"); // apenas 7 caracteres
        cbCargo.setSelectedItem("TECNICO");

        UsuarioControler.cadastrarNovoUsuario(txtNomeCad, txtLoginCad, txtSenhaCad,
                cbCargo, lblErroCad, mockFrame);

        assertFalse(lblErroCad.getText().isEmpty(),
                "Deve haver mensagem de erro para senha com menos de 8 caracteres");
        assertTrue(lblErroCad.getText().toLowerCase().contains("senha") ||
                   lblErroCad.getText().toLowerCase().contains("8"),
                "Mensagem deve mencionar o tamanho mínimo da senha");
    }

    // -----------------------------------------------------------------------
    // CT-U06: Roteamento por perfil — ATENDENTE abre TelaAtendente
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U06 - Login de ATENDENTE deve abrir TelaAtendente")
    void ctU06_roteamentoPorPerfilAtendente() {

        Usuario atendente = criarUsuario("ana.atend", "ATENDENTE");

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> when(mock.autenticarUsuario(anyString(), anyString()))
                        .thenReturn(atendente));
             MockedConstruction<TelaAtendente> mockedTela = mockConstruction(TelaAtendente.class)) {

            txtLogin.setText("ana.atend");
            txtSenha.setText("qualquerSenha");

            uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);
            aguardarEDT();

            assertFalse(mockedTela.constructed().isEmpty(),
                    "TelaAtendente deve ser instanciada para cargo ATENDENTE");
        }
    }

    // -----------------------------------------------------------------------
    // CT-U07: Roteamento por perfil — TECNICO abre TelaMenuTecnico
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-U07 - Login de TECNICO deve abrir TelaMenuTecnico")
    void ctU07_roteamentoPorPerfilTecnico() {

        Usuario tecnico = criarUsuario("tec.silva", "TECNICO");

        try (MockedConstruction<UsuarioDao> mockedDao = mockConstruction(UsuarioDao.class,
                (mock, ctx) -> when(mock.autenticarUsuario(anyString(), anyString()))
                        .thenReturn(tecnico));
             MockedConstruction<TelaMenuTecnico> mockedTela = mockConstruction(TelaMenuTecnico.class)) {

            txtLogin.setText("tec.silva");
            txtSenha.setText("qualquerSenha");

            uc.efetuarLogin(txtLogin, txtSenha, lblErroLogin);
            aguardarEDT();

            assertFalse(mockedTela.constructed().isEmpty(),
                    "TelaMenuTecnico deve ser instanciada para cargo TECNICO");
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void preencherCadastroValido() {
        txtNomeCad.setText("Fernanda Santos");
        txtLoginCad.setText("fernanda");
        txtSenhaCad.setText("Senha@2024");
        cbCargo.setSelectedItem("ATENDENTE");
    }

    private Usuario criarUsuario(String login, String cargo) {
        Usuario u = new Usuario();
        u.setIdUsuario(99);
        u.setNome("Usuário Teste");
        u.setLogin(login);
        u.setCargo(cargo);
        return u;
    }

    /**
     * Aguarda a thread de eventos do Swing (EDT) processar as tarefas pendentes.
     * Necessário após chamadas a SwingUtilities.invokeLater().
     */
    private void aguardarEDT() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                // bloco vazio: só garante que tarefas anteriores do EDT terminaram
            });
        } catch (Exception e) {
            // Em modo headless o EDT pode não existir — apenas continua
        }
    }
}

package com.senacsoluctions.controler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.senacsoluctions.BaseTest;
import com.senacsoluctions.dao.ClienteDao;
import com.senacsoluctions.model.Cliente;

import javax.swing.JOptionPane;

/**
 * Testes unitários do módulo de Clientes.
 * CT-C01 a CT-C05 — cobertura de cadastro válido, CPF inválido/duplicado,
 * telefone incompleto e campos obrigatórios vazios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo de Clientes")
class ClienteControlerTest extends BaseTest {

    private ClienteControler controler;

    // Componentes Swing criados uma vez por teste (headless)
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JLabel lblErro;
    private JFrame mockFrame;

    @BeforeEach
    void setUp() {
        controler = new ClienteControler();
        txtNome     = new JTextField();
        txtCpf      = new JTextField();
        txtTelefone = new JTextField();
        txtEmail    = new JTextField();
        lblErro     = new JLabel();
        mockFrame   = mock(JFrame.class); // mock evita tentar abrir janela real
    }

    // -----------------------------------------------------------------------
    // CT-C01: Cadastrar cliente com dados válidos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-C01 - Cadastrar cliente com dados válidos deve exibir mensagem de sucesso")
    void ctC01_cadastrarClienteComDadosValidos() {

        try (MockedConstruction<ClienteDao> mockedDao = mockConstruction(ClienteDao.class,
                (mock, ctx) -> {
                    when(mock.buscarPorCpf(anyString())).thenReturn(null); // sem duplicata
                    when(mock.salvarCliente(any(Cliente.class))).thenReturn(true);
                });
             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {

            preencherCamposValidos();

            controler.cadastrarCliente(txtNome, txtCpf, txtTelefone, txtEmail, lblErro, mockFrame);

            // Sem erro na label
            assertEquals("", lblErro.getText(),
                    "Nenhuma mensagem de erro deve ser exibida para dados válidos");

            // Deve ter pedido ao DAO para salvar
            ClienteDao daoInstanciado = mockedDao.constructed().get(0);
            verify(daoInstanciado, times(1)).salvarCliente(any(Cliente.class));

            // Deve ter exibido diálogo de sucesso
            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(any(), contains("sucesso"), any(), anyInt()),
                    times(1));
        }
    }

    // -----------------------------------------------------------------------
    // CT-C02: Bloquear CPF com dígito verificador inválido
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-C02 - CPF com dígito verificador inválido deve ser rejeitado")
    void ctC02_bloquearCpfComDigitoVerificadorInvalido() {
        // CPF com formato correto mas dígito verificador errado
        assertFalse(ClienteControler.isCpfValido("123.456.789-09"),
                "CPF com dígito verificador inválido deve retornar false");
    }

    @Test
    @DisplayName("CT-C02b - CPF inválido na tela deve exibir mensagem de erro no campo")
    void ctC02b_cpfInvalidoNaTelaExibeMensagemDeErro() {
        txtNome.setText("João Silva");
        txtCpf.setText("123.456.789-09"); // dígito errado
        txtTelefone.setText("(47) 99999-9999");
        txtEmail.setText("joao@email.com");

        controler.cadastrarCliente(txtNome, txtCpf, txtTelefone, txtEmail, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro para CPF inválido");
        assertTrue(lblErro.getText().toLowerCase().contains("inválido") ||
                   lblErro.getText().toLowerCase().contains("invalido"),
                "Mensagem deve mencionar CPF inválido");
    }

    // -----------------------------------------------------------------------
    // CT-C03: Bloquear CPF duplicado
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-C03 - CPF já cadastrado deve exibir mensagem de duplicidade")
    void ctC03_bloquearCpfDuplicado() {

        Cliente clienteExistente = new Cliente();
        clienteExistente.setNome("Cliente Existente");
        clienteExistente.setCpf("52998224725");

        try (MockedConstruction<ClienteDao> mockedDao = mockConstruction(ClienteDao.class,
                (mock, ctx) -> when(mock.buscarPorCpf(anyString())).thenReturn(clienteExistente))) {

            preencherCamposValidos(); // CPF 529.982.247-25 (válido, mas já existe no mock)

            controler.cadastrarCliente(txtNome, txtCpf, txtTelefone, txtEmail, lblErro, mockFrame);

            assertFalse(lblErro.getText().isEmpty(),
                    "Deve haver mensagem de erro para CPF duplicado");
            assertTrue(lblErro.getText().contains("CPF") || lblErro.getText().contains("cadastrado"),
                    "Mensagem deve mencionar que o CPF já está cadastrado");

            // DAO não deve ter chamado salvarCliente
            ClienteDao daoInstanciado = mockedDao.constructed().get(0);
            verify(daoInstanciado, never()).salvarCliente(any());
        }
    }

    // -----------------------------------------------------------------------
    // CT-C04: Bloquear telefone incompleto (menos de 11 dígitos)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-C04 - Telefone com menos de 11 dígitos deve ser rejeitado")
    void ctC04_bloquearTelefoneIncompleto() {

        try (MockedConstruction<ClienteDao> ignored = mockConstruction(ClienteDao.class,
                (mock, ctx) -> when(mock.buscarPorCpf(anyString())).thenReturn(null))) {

            txtNome.setText("Maria Oliveira");
            txtCpf.setText("529.982.247-25");
            txtTelefone.setText("(47) 9999-999");  // apenas 10 dígitos — incompleto
            txtEmail.setText("maria@email.com");

            controler.cadastrarCliente(txtNome, txtCpf, txtTelefone, txtEmail, lblErro, mockFrame);

            assertFalse(lblErro.getText().isEmpty(),
                    "Deve haver mensagem de erro para telefone incompleto");
        }
    }

    // -----------------------------------------------------------------------
    // CT-C05: Bloquear campos obrigatórios vazios (nome)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-C05 - Submeter formulário sem nome deve exibir erro no campo nome")
    void ctC05_bloquearCampoNomeVazio() {
        txtNome.setText("");           // nome vazio
        txtCpf.setText("529.982.247-25");
        txtTelefone.setText("(47) 99999-9999");
        txtEmail.setText("teste@email.com");

        controler.cadastrarCliente(txtNome, txtCpf, txtTelefone, txtEmail, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro quando o nome está vazio");
        assertTrue(lblErro.getText().toLowerCase().contains("nome"),
                "Mensagem deve mencionar o campo nome");
    }

    // -----------------------------------------------------------------------
    // Testes auxiliares para os métodos utilitários do ClienteControler
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("apenasNumeros - deve remover máscara e retornar somente dígitos")
    void apenasNumeros_deveRetornarApenasDigitos() {
        assertEquals("52998224725", ClienteControler.apenasNumeros("529.982.247-25"));
        assertEquals("47999998888", ClienteControler.apenasNumeros("(47) 99999-8888"));
        assertEquals("", ClienteControler.apenasNumeros(null));
    }

    @Test
    @DisplayName("isCpfValido - sequências repetidas devem ser rejeitadas")
    void isCpfValido_sequenciasRepetidasSaoInvalidas() {
        assertFalse(ClienteControler.isCpfValido("111.111.111-11"));
        assertFalse(ClienteControler.isCpfValido("000.000.000-00"));
        assertFalse(ClienteControler.isCpfValido("999.999.999-99"));
    }

    @Test
    @DisplayName("isCpfValido - CPF matematicamente correto deve ser aceito")
    void isCpfValido_cpfCorretoDeverSerAceito() {
        assertTrue(ClienteControler.isCpfValido("529.982.247-25")); // CPF válido conhecido
        assertTrue(ClienteControler.isCpfValido("52998224725"));     // sem máscara
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private void preencherCamposValidos() {
        txtNome.setText("Ana Paula Costa");
        txtCpf.setText("529.982.247-25");        // CPF válido
        txtTelefone.setText("(47) 99999-8888");
        txtEmail.setText("ana@email.com");
    }
}

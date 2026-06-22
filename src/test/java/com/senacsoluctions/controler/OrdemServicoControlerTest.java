package com.senacsoluctions.controler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.senacsoluctions.BaseTest;
import com.senacsoluctions.dao.OrdemServicoDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.StatusOS;
import com.senacsoluctions.model.Usuario;
import com.senacsoluctions.view.Utils;

/**
 * Testes unitários do módulo de Ordens de Serviço.
 * CT-OS01, CT-OS02, CT-OS03, CT-OS06, CT-OS09.
 *
 * CT-OS04, CT-OS05, CT-OS07 e CT-OS08 exigem banco de dados e estão em
 * OrdemServicoDaoIntegrationTest.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo de Ordens de Serviço — Controller")
class OrdemServicoControlerTest extends BaseTest {

    private OrdemServicoControler controler;

    // Componentes Swing
    private JTextField txtCusto;
    private JTextArea txtDescricaoDefeito;
    private JLabel lblErro;
    private JFrame mockFrame;

    // Objetos de modelo
    private Cliente clienteValido;
    private Equipamento equipamentoValido;
    private JComboBox<Equipamento> cbEquipamento;

    @BeforeEach
    void setUp() {
        controler = new OrdemServicoControler();

        txtCusto           = new JTextField();
        txtDescricaoDefeito = new JTextArea();
        lblErro            = new JLabel();
        mockFrame          = mock(JFrame.class);

        clienteValido = new Cliente();
        clienteValido.setIdCliente(1);
        clienteValido.setNome("Carlos Lima");
        clienteValido.setCpf("52998224725");
        clienteValido.setTelefone("47999998888");
        clienteValido.setEmail("carlos@email.com");

        equipamentoValido = new Equipamento();
        equipamentoValido.setIdEquipamento(10);
        equipamentoValido.setTipo("Notebook");
        equipamentoValido.setMarca("Dell");
        equipamentoValido.setModelo("Inspiron 15");
        equipamentoValido.setNumSerie("SN-DELL-001");
        equipamentoValido.setCliente(clienteValido);

        cbEquipamento = new JComboBox<>();
        cbEquipamento.addItem(equipamentoValido);
    }

    // -----------------------------------------------------------------------
    // CT-OS01: Abrir OS com dados válidos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS01 - OS com dados válidos deve ser criada e número exibido")
    void ctOs01_abrirOsComDadosValidos() {

        try (MockedConstruction<OrdemServicoDao> mockedDao = mockConstruction(OrdemServicoDao.class,
                (mock, ctx) -> when(mock.salvarOS(any(OrdemServico.class))).thenAnswer(inv -> {
                    // Simula o banco atribuindo o num_os e retornando true
                    OrdemServico os = inv.getArgument(0);
                    os.setNumOs(42);
                    return true;
                }));
             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {

            txtCusto.setText("350.00");
            txtDescricaoDefeito.setText("Notebook não liga após queda");

            controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                    txtDescricaoDefeito, lblErro, mockFrame);

            assertEquals("", lblErro.getText(), "Não deve haver erro para OS válida");

            // Verifica que o DAO recebeu a chamada de salvar
            OrdemServicoDao daoInstanciado = mockedDao.constructed().get(0);
            verify(daoInstanciado, times(1)).salvarOS(any(OrdemServico.class));

            // Verifica que o diálogo de sucesso mencionou o número da OS
            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(any(), contains("42"), any(), anyInt()),
                    times(1));
        }
    }

    // -----------------------------------------------------------------------
    // CT-OS02: Bloquear OS sem descrição do defeito
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS02 - OS sem descrição do defeito deve ser bloqueada")
    void ctOs02_bloquearOsSemDescricaoDefeito() {

        txtCusto.setText("150.00");
        txtDescricaoDefeito.setText(""); // campo obrigatório vazio

        controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                txtDescricaoDefeito, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro quando a descrição do defeito está vazia");
    }

    @Test
    @DisplayName("CT-OS02b - OS sem cliente localizado deve ser bloqueada")
    void ctOs02b_bloquearOsSemClienteLocalizado() {

        txtCusto.setText("150.00");
        txtDescricaoDefeito.setText("Tela quebrada");

        // clienteSelecionado = null (usuário não buscou cliente antes)
        controler.emitirOrdemServico(null, cbEquipamento, txtCusto,
                txtDescricaoDefeito, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro quando nenhum cliente foi localizado");
    }

    @Test
    @DisplayName("CT-OS02c - OS com custo inválido (texto) deve ser bloqueada")
    void ctOs02c_bloquearOsComCustoInvalido() {

        txtCusto.setText("abc"); // não numérico
        txtDescricaoDefeito.setText("Tela quebrada");

        controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                txtDescricaoDefeito, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro para custo não numérico");
    }

    @Test
    @DisplayName("CT-OS02d - OS com custo negativo deve ser bloqueada")
    void ctOs02d_bloquearOsComCustoNegativo() {

        txtCusto.setText("-50.00");
        txtDescricaoDefeito.setText("Placa queimada");

        controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                txtDescricaoDefeito, lblErro, mockFrame);

        assertFalse(lblErro.getText().isEmpty(),
                "Deve haver mensagem de erro para custo negativo");
    }

    // -----------------------------------------------------------------------
    // CT-OS03: Verificar que o prazo é de exatamente 15 dias a partir da abertura
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS03 - data_limite deve ser data_abertura + 15 dias")
    void ctOs03_prazoDeQuinzeDias() {

        try (MockedConstruction<OrdemServicoDao> mockedDao = mockConstruction(OrdemServicoDao.class,
                (mock, ctx) -> when(mock.salvarOS(any(OrdemServico.class))).thenReturn(true));
             MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {

            txtCusto.setText("200.00");
            txtDescricaoDefeito.setText("Bateria não carrega");

            Date antes = new Date();

            controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                    txtDescricaoDefeito, lblErro, mockFrame);

            // Captura o objeto OS que foi passado ao DAO
            OrdemServicoDao daoInstanciado = mockedDao.constructed().get(0);

            org.mockito.ArgumentCaptor<OrdemServico> captor =
                    org.mockito.ArgumentCaptor.forClass(OrdemServico.class);
            verify(daoInstanciado).salvarOS(captor.capture());

            OrdemServico osSalva = captor.getValue();

            // Calcula o prazo esperado
            Calendar calEsperado = Calendar.getInstance();
            calEsperado.setTime(antes);
            calEsperado.add(Calendar.DAY_OF_MONTH, 15);

            // Compara apenas o dia (ignora hora/minuto/segundo)
            Calendar calLimite = Calendar.getInstance();
            calLimite.setTime(osSalva.getDataLimite());

            assertEquals(calEsperado.get(Calendar.DAY_OF_MONTH), calLimite.get(Calendar.DAY_OF_MONTH),
                    "Dia do prazo limite deve ser abertura + 15 dias");
            assertEquals(calEsperado.get(Calendar.MONTH), calLimite.get(Calendar.MONTH),
                    "Mês do prazo limite deve corresponder ao esperado");
        }
    }

    @Test
    @DisplayName("CT-OS03b - Status inicial da OS deve ser EM_ABERTO")
    void ctOs03b_statusInicialDeveSerEmAberto() {

        try (MockedConstruction<OrdemServicoDao> mockedDao = mockConstruction(OrdemServicoDao.class,
                (mock, ctx) -> when(mock.salvarOS(any())).thenReturn(true));
             MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {

            txtCusto.setText("200.00");
            txtDescricaoDefeito.setText("Teclado preso");

            controler.emitirOrdemServico(clienteValido, cbEquipamento, txtCusto,
                    txtDescricaoDefeito, lblErro, mockFrame);

            OrdemServicoDao daoInstanciado = mockedDao.constructed().get(0);
            org.mockito.ArgumentCaptor<OrdemServico> captor =
                    org.mockito.ArgumentCaptor.forClass(OrdemServico.class);
            verify(daoInstanciado).salvarOS(captor.capture());

            assertEquals(StatusOS.EM_ABERTO, captor.getValue().getStatus(),
                    "OS deve nascer com status EM_ABERTO");
            assertNull(captor.getValue().getTecnico(),
                    "OS deve nascer sem técnico atribuído");
        }
    }

    // -----------------------------------------------------------------------
    // CT-OS06: Sistema de cores de prioridade (Utils.corPrioridade)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS06 - OS com 3 dias restantes deve ter cor VERMELHA")
    void ctOs06_osCom3DiasRestantesDeveSerVermelha() {
        Date dataLimite = dataRelativa(3);
        Color cor = Utils.corPrioridade(dataLimite);

        // Vermelho: R > G e R > B
        assertTrue(cor.getRed() > cor.getGreen() && cor.getRed() > cor.getBlue(),
                "OS com 3 dias restantes deve retornar uma cor vermelha");
    }

    @Test
    @DisplayName("CT-OS06b - OS com 8 dias restantes deve ter cor AMARELA")
    void ctOs06b_osCom8DiasRestantesDeveSerAmarela() {
        Date dataLimite = dataRelativa(8);
        Color cor = Utils.corPrioridade(dataLimite);

        // Amarelo: R alto, G alto, B baixo
        assertTrue(cor.getRed() > 100 && cor.getGreen() > 100 && cor.getBlue() < 100,
                "OS com 8 dias restantes deve retornar uma cor amarela");
    }

    @Test
    @DisplayName("CT-OS06c - OS com 13 dias restantes deve ter cor VERDE")
    void ctOs06c_osCom13DiasRestantesDeveSerVerde() {
        Date dataLimite = dataRelativa(13);
        Color cor = Utils.corPrioridade(dataLimite);

        // Verde: G > R
        assertTrue(cor.getGreen() > cor.getRed(),
                "OS com 13 dias restantes deve retornar uma cor verde");
    }

    @Test
    @DisplayName("CT-OS06d - OS com prazo vencido deve ter cor VERMELHO ESCURO")
    void ctOs06d_osPrazoVencidoDeveSerVermelho() {
        Date dataLimite = dataRelativa(-2); // atrasada
        Color cor = Utils.corPrioridade(dataLimite);
        assertTrue(cor.getRed() > cor.getGreen(),
                "OS atrasada deve retornar uma cor avermelhada");
    }

    // -----------------------------------------------------------------------
    // CT-OS09: Busca de OS pendentes por CPF do cliente
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS09 - listarPendentesPorCliente deve retornar lista com OS do cliente")
    void ctOs09_listarPendentesPorClienteDeveRetornarOsDoCliente() {

        OrdemServico osFake = criarOsFake(1, StatusOS.EM_ANDAMENTO);

        try (MockedConstruction<OrdemServicoDao> mockedDao = mockConstruction(OrdemServicoDao.class,
                (mock, ctx) -> when(mock.listarPendentesPorCliente(clienteValido.getIdCliente()))
                        .thenReturn(Arrays.asList(osFake)))) {

            List<OrdemServico> resultado = controler.listarPendentesPorCliente(clienteValido);

            assertNotNull(resultado, "Resultado não deve ser null");
            assertEquals(1, resultado.size(), "Deve retornar 1 OS para o cliente");
            assertEquals(StatusOS.EM_ANDAMENTO, resultado.get(0).getStatus());
        }
    }

    @Test
    @DisplayName("CT-OS09b - listarPendentesPorCliente com cliente null deve retornar lista vazia")
    void ctOs09b_clienteNullDeveRetornarListaVazia() {
        List<OrdemServico> resultado = controler.listarPendentesPorCliente(null);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "Cliente null deve retornar lista vazia sem lançar exceção");
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static Date dataRelativa(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    private OrdemServico criarOsFake(int numOs, String status) {
        OrdemServico os = new OrdemServico();
        os.setNumOs(numOs);
        os.setStatus(status);
        os.setDataAbertura(new Date());
        os.setDataLimite(dataRelativa(10));
        os.setCliente(clienteValido);
        os.setEquipamento(equipamentoValido);
        os.setDescricaoDefeito("Defeito teste");
        os.setCusto(100.0);
        return os;
    }
}

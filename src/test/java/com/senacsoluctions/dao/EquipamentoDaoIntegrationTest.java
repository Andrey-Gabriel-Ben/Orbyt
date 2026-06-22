package com.senacsoluctions.dao;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.senacsoluctions.BaseTest;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;


@Tag("integration")
@DisplayName("EquipamentoDao — Testes de Integração")
class EquipamentoDaoIntegrationTest extends BaseTest {

    private EquipamentoDao equipamentoDao;
    private ClienteDao clienteDao;

    private int idClienteTeste;
    private String numSerieUnico;

    @BeforeEach
    void setUp() {
        equipamentoDao = new EquipamentoDao();
        clienteDao     = new ClienteDao();

        // Cria cliente de teste (ou reutilize um fixo do banco de testes)
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Equip JUnit");
        cliente.setCpf("00011122233");
        cliente.setTelefone("47988887777");
        cliente.setEmail("equip@junit.com");
        clienteDao.salvarCliente(cliente);

        Cliente salvo = clienteDao.buscarPorCpf("00011122233");
        assertNotNull(salvo, "Cliente de suporte deve existir para testar equipamentos");
        idClienteTeste = salvo.getIdCliente();

        // Número de série único por execução (evita conflito entre runs)
        numSerieUnico = "SN-JUNIT-EQ-" + System.currentTimeMillis();
    }

    @AfterEach
    void tearDown() {
        try (java.sql.Connection conn = ConexaoBanco.getConexao();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM equipamento WHERE num_serie LIKE 'SN-JUNIT-EQ-%'");
            stmt.execute("DELETE FROM cliente WHERE cpf = '00011122233'");
        } catch (Exception e) {
            System.err.println("Aviso: erro na limpeza de equipamentos — " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // CT-E01: Cadastrar equipamento vinculado a um cliente
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-E01 - Equipamento deve ser salvo e associado ao cliente")
    void ctE01_cadastrarEquipamentoVinculadoACliente() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idClienteTeste);

        Equipamento equip = new Equipamento();
        equip.setTipo("Celular");
        equip.setMarca("Samsung");
        equip.setModelo("Galaxy A54");
        equip.setNumSerie(numSerieUnico);
        equip.setCliente(cliente);

        boolean salvo = equipamentoDao.salvar(equip);

        assertTrue(salvo, "salvar() deve retornar true para equipamento válido");
        assertTrue(equip.getIdEquipamento() > 0,
                "DAO deve popular o id gerado pelo banco no objeto");

        // Verifica que aparece na listagem do cliente
        List<Equipamento> lista = equipamentoDao.listarPorCliente(idClienteTeste);
        assertTrue(lista.stream().anyMatch(e -> e.getNumSerie().equals(numSerieUnico)),
                "Equipamento salvo deve aparecer na listagem do cliente");
    }

    // -----------------------------------------------------------------------
    // CT-E02: Bloquear número de série duplicado (UNIQUE constraint)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-E02 - Número de série duplicado deve ser rejeitado pelo banco")
    void ctE02_bloquearNumeroSerieDuplicado() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idClienteTeste);

        // Primeiro equipamento com o número de série
        Equipamento primeiro = new Equipamento();
        primeiro.setTipo("Notebook");
        primeiro.setMarca("Dell");
        primeiro.setModelo("Inspiron");
        primeiro.setNumSerie(numSerieUnico);
        primeiro.setCliente(cliente);
        boolean primeiraSalva = equipamentoDao.salvar(primeiro);
        assertTrue(primeiraSalva, "Primeiro equipamento deve ser salvo com sucesso");

        // Segundo equipamento com o MESMO número de série
        Equipamento segundo = new Equipamento();
        segundo.setTipo("Notebook");
        segundo.setMarca("Lenovo");
        segundo.setModelo("ThinkPad");
        segundo.setNumSerie(numSerieUnico); // número de série já existente
        segundo.setCliente(cliente);

        boolean segundaSalva = equipamentoDao.salvar(segundo);
        assertFalse(segundaSalva,
                "Segundo equipamento com o mesmo número de série deve ser rejeitado pelo DAO");
    }

    // -----------------------------------------------------------------------
    // CT-E03: listarPorCliente deve popular o ComboBox da tela de OS
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-E03 - listarPorCliente deve retornar equipamentos do cliente para o ComboBox")
    void ctE03_listarEquipamentosDoClienteParaComboBox() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idClienteTeste);

        // Cadastra dois equipamentos para o mesmo cliente
        for (int i = 1; i <= 2; i++) {
            Equipamento e = new Equipamento();
            e.setTipo("Tablet");
            e.setMarca("Apple");
            e.setModelo("iPad " + i);
            e.setNumSerie(numSerieUnico + "-" + i);
            e.setCliente(cliente);
            equipamentoDao.salvar(e);
        }

        List<Equipamento> lista = equipamentoDao.listarPorCliente(idClienteTeste);

        assertNotNull(lista, "Lista não deve ser null");
        assertTrue(lista.size() >= 2,
                "Deve retornar pelo menos os 2 equipamentos cadastrados no teste");

        // Simula o preenchimento do JComboBox (como feito na TelaCadastroOS)
        javax.swing.JComboBox<Equipamento> cbEquip = new javax.swing.JComboBox<>();
        for (Equipamento eq : lista) cbEquip.addItem(eq);

        assertTrue(cbEquip.getItemCount() >= 2,
                "JComboBox deve conter ao menos 2 itens após ser populado");

        // toString() dos itens deve exibir informações legíveis (não null)
        for (int i = 0; i < cbEquip.getItemCount(); i++) {
            assertNotNull(cbEquip.getItemAt(i).toString());
            assertFalse(cbEquip.getItemAt(i).toString().isBlank());
        }
    }
}

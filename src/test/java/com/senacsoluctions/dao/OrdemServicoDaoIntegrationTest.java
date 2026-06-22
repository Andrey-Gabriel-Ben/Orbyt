package com.senacsoluctions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.StatusOS;


@Tag("integration")
@DisplayName("OrdemServicoDao — Testes de Integração")
class OrdemServicoDaoIntegrationTest extends BaseTest {

    private OrdemServicoDao osDao;
    private ClienteDao clienteDao;
    private EquipamentoDao equipamentoDao;
    private UsuarioDao usuarioDao;

    // IDs criados durante o teste — usados para limpeza no @AfterEach
    private int idClienteTeste;
    private int idEquipamentoTeste;
    private int idTecnico1;
    private int idTecnico2;
    private int numOsTeste;

    @BeforeEach
    void setUp() throws Exception {
        osDao          = new OrdemServicoDao();
        clienteDao     = new ClienteDao();
        equipamentoDao = new EquipamentoDao();
        usuarioDao     = new UsuarioDao();

        // Insere dados mínimos para os testes (evita poluir o banco de produção)
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste JUnit");
        cliente.setCpf("00000000191"); // CPF válido fictício para testes
        cliente.setTelefone("47900000000");
        cliente.setEmail("junit@teste.com");
        clienteDao.salvarCliente(cliente);

        // Busca o cliente recém-criado pelo CPF para obter o id gerado
        Cliente clienteSalvo = clienteDao.buscarPorCpf("00000000191");
        assertNotNull(clienteSalvo, "Cliente de teste deve ter sido salvo");
        idClienteTeste = clienteSalvo.getIdCliente();

        Equipamento equip = new Equipamento();
        equip.setTipo("Notebook");
        equip.setMarca("TestBrand");
        equip.setModelo("TestModel");
        equip.setNumSerie("SN-JUNIT-" + System.currentTimeMillis()); // garante unicidade
        equip.setCliente(clienteSalvo);
        equipamentoDao.salvar(equip);
        idEquipamentoTeste = equip.getIdEquipamento();

        // Cria dois técnicos de teste para CT-OS05
        // (na prática, crie-os manualmente no banco de teste se preferir)
        idTecnico1 = buscarOuUsarTecnicoFixo(1);
        idTecnico2 = buscarOuUsarTecnicoFixo(2);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Limpa os registros criados durante o teste para não poluir o banco
        try (java.sql.Connection conn = ConexaoBanco.getConexao();
             java.sql.Statement stmt = conn.createStatement()) {

            if (numOsTeste > 0)
                stmt.execute("DELETE FROM ordem_servico WHERE num_os = " + numOsTeste);
            if (idEquipamentoTeste > 0)
                stmt.execute("DELETE FROM equipamento WHERE id_equipamento = " + idEquipamentoTeste);
            if (idClienteTeste > 0)
                stmt.execute("DELETE FROM cliente WHERE id_cliente = " + idClienteTeste);
        } catch (Exception e) {
            System.err.println("Aviso: erro na limpeza do banco de testes — " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // CT-OS04: Técnico assume OS da fila (status → EM_ANDAMENTO, id_tecnico vinculado)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS04 - Técnico assume OS da fila deve alterar status para EM_ANDAMENTO")
    void ctOs04_tecnicoAssumeOsDaFila() {
        OrdemServico os = criarOsEmAberto();
        numOsTeste = os.getNumOs();
        assertTrue(numOsTeste > 0, "OS deve ter sido salva com número gerado pelo banco");

        boolean assumiu = osDao.assumirOS(numOsTeste, idTecnico1);
        assertTrue(assumiu, "assumirOS deve retornar true para OS em aberto sem técnico");

        OrdemServico osAtualizada = osDao.buscarPorNumOs(numOsTeste);
        assertNotNull(osAtualizada);
        assertEquals(StatusOS.EM_ANDAMENTO, osAtualizada.getStatus(),
                "Status deve ser EM_ANDAMENTO após técnico assumir a OS");
        assertNotNull(osAtualizada.getTecnico(), "Técnico deve estar vinculado à OS");
        assertEquals(idTecnico1, osAtualizada.getTecnico().getIdUsuario(),
                "O id do técnico vinculado deve ser o do técnico que assumiu");
    }

    // -----------------------------------------------------------------------
    // CT-OS05: Evitar que dois técnicos assumam a mesma OS simultaneamente
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS05 - Apenas o primeiro técnico deve conseguir assumir a OS")
    void ctOs05_apenasUmTecnicoPodeAssumirAMesmaOs() {
        OrdemServico os = criarOsEmAberto();
        numOsTeste = os.getNumOs();

        boolean tecnico1Assumiu = osDao.assumirOS(numOsTeste, idTecnico1);
        boolean tecnico2Assumiu = osDao.assumirOS(numOsTeste, idTecnico2);

        assertTrue(tecnico1Assumiu, "Técnico 1 deve conseguir assumir a OS");
        assertFalse(tecnico2Assumiu,
                "Técnico 2 NÃO deve conseguir assumir a mesma OS (WHERE id_tecnico IS NULL)");

        // Confirma que o técnico 1 está vinculado
        OrdemServico osAtualizada = osDao.buscarPorNumOs(numOsTeste);
        assertEquals(idTecnico1, osAtualizada.getTecnico().getIdUsuario());
    }

    // -----------------------------------------------------------------------
    // CT-OS07: Técnico finaliza a OS (status → FINALIZADA)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS07 - Finalizar OS deve alterar status para FINALIZADA")
    void ctOs07_finalizarOsDeveAlterarStatusParaFinalizada() {
        OrdemServico os = criarOsEmAberto();
        numOsTeste = os.getNumOs();

        // Técnico assume a OS primeiro
        osDao.assumirOS(numOsTeste, idTecnico1);

        // Técnico finaliza
        boolean finalizado = osDao.finalizarOS(numOsTeste);
        assertTrue(finalizado, "finalizarOS deve retornar true");

        OrdemServico osAtualizada = osDao.buscarPorNumOs(numOsTeste);
        assertEquals(StatusOS.FINALIZADA, osAtualizada.getStatus(),
                "Status deve ser FINALIZADA após o técnico concluir o serviço");

        // OS FINALIZADA deve aparecer na lista de pendentes de entrega (tela do atendente T1)
        List<OrdemServico> pendentesEntrega = osDao.listarFinalizadasPendentesEntrega();
        assertTrue(pendentesEntrega.stream().anyMatch(o -> o.getNumOs() == numOsTeste),
                "OS FINALIZADA deve aparecer na lista de pendentes de entrega");
    }

    // -----------------------------------------------------------------------
    // CT-OS08: Atendente confirma entrega (status → CONCLUIDA)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CT-OS08 - Confirmar entrega deve alterar status para CONCLUIDA")
    void ctOs08_confirmarEntregaDeveAlterarStatusParaConcluida() {
        OrdemServico os = criarOsEmAberto();
        numOsTeste = os.getNumOs();

        // Simula o fluxo completo: aberta → em andamento → finalizada → concluída
        osDao.assumirOS(numOsTeste, idTecnico1);
        osDao.finalizarOS(numOsTeste);

        boolean concluido = osDao.marcarComoConcluida(numOsTeste);
        assertTrue(concluido, "marcarComoConcluida deve retornar true");

        OrdemServico osAtualizada = osDao.buscarPorNumOs(numOsTeste);
        assertEquals(StatusOS.CONCLUIDA, osAtualizada.getStatus(),
                "Status deve ser CONCLUIDA após atendente confirmar retirada");

        // OS CONCLUIDA NÃO deve aparecer mais na lista de pendentes de entrega
        List<OrdemServico> pendentesEntrega = osDao.listarFinalizadasPendentesEntrega();
        assertFalse(pendentesEntrega.stream().anyMatch(o -> o.getNumOs() == numOsTeste),
                "OS CONCLUIDA não deve aparecer na lista de pendentes de entrega");
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private OrdemServico criarOsEmAberto() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idClienteTeste);

        Equipamento equip = new Equipamento();
        equip.setIdEquipamento(idEquipamentoTeste);
        equip.setCliente(cliente);

        Date agora = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);

        OrdemServico os = new OrdemServico();
        os.setDataAbertura(agora);
        os.setDataLimite(cal.getTime());
        os.setStatus(StatusOS.EM_ABERTO);
        os.setDescricaoDefeito("Defeito criado pelo JUnit para CT-OS04/05/07/08");
        os.setCusto(0.0);
        os.setCliente(cliente);
        os.setEquipamento(equip);
        os.setTecnico(null);

        boolean salvo = osDao.salvarOS(os);
        assertTrue(salvo, "OS de teste deve ser salva com sucesso no banco");
        return os;
    }

    /**
     * Retorna o id de um técnico fixo já existente no banco de testes.
     * Ajuste os IDs conforme o seu banco de testes.
     */
    private int buscarOuUsarTecnicoFixo(int indice) {
        // Para o banco de testes, assuma que existem técnicos com id 1 e 2.
        // Se quiser criar dinamicamente, implemente a lógica aqui.
        return indice; // id_usuario = 1 e id_usuario = 2
    }
}

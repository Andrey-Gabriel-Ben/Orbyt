package com.senacsoluctions.controler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.senacsoluctions.dao.OrdemServicoDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.model.Equipamento;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.StatusOS;
import com.senacsoluctions.model.Usuario;
import com.senacsoluctions.view.Utils;

public class OrdemServicoControler {

    private OrdemServicoDao osDao = new OrdemServicoDao();

    public void emitirOrdemServico(Cliente clienteLocalizado, JComboBox<Equipamento> cbEquipamento, JTextField txtCusto,
            JTextArea txtDescricaoDefeito, JLabel lblMensagemErro, JFrame telaAtual) {

        // Limpando estados de erro visuais anteriores
        cbEquipamento.putClientProperty("JComponent.outline", null);
        txtCusto.putClientProperty("JComponent.outline", null);
        lblMensagemErro.setText("");

        // 1. Verificação: Se o usuário não buscou/encontrou um cliente antes
        if (clienteLocalizado == null) {
            lblMensagemErro.setText("Por favor, localize um cliente válido por CPF primeiro!");
            return;
        }

        // 2. Verificação: Equipamento/Aparelho obrigatório
        Equipamento equipSelected = (Equipamento) cbEquipamento.getSelectedItem();
        if (equipSelected == null) {
            Utils.mostrarErro(lblMensagemErro, cbEquipamento,
                    "Selecione ou cadastre um equipamento para este cliente!");
            return;
        }

        // 3. Verificação da Descrição do Defeito (descricao_defeito é NOT NULL no
        // banco)
        String descricaoDefeito = txtDescricaoDefeito.getText().trim();
        if (descricaoDefeito.isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtDescricaoDefeito, "Descreva o defeito relatado pelo cliente!");
            return;
        }

        // 4. Verificação e Conversão do Custo Estimado
        String custoTexto = txtCusto.getText().trim();
        double custo = 0.0;

        if (custoTexto.isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtCusto, "O campo Custo Estimado é obrigatório!");
            return;
        }

        try {
            // Substitui a vírgula por ponto para evitar NumberFormatException
            custo = Double.parseDouble(custoTexto.replace(",", "."));
            if (custo < 0) {
                Utils.mostrarErro(lblMensagemErro, txtCusto, "O custo estimado não pode ser negativo!");
                return;
            }
        } catch (NumberFormatException ex) {
            Utils.mostrarErro(lblMensagemErro, txtCusto, "Digite um valor numérico válido para o custo! (Ex: 150.00)");
            return;
        }

        // --- SE PASSOU NAS VALIDAÇÕES DE REQUISITOS ---

        Date dataAbertura = new Date(); // Data atual do sistema

        // Calculando os 15 dias de prazo máximo usando Calendar (RN02)
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataAbertura);
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date dataLimite = cal.getTime();

        // 5. Montagem do Objeto de Modelo
        OrdemServico novaOs = new OrdemServico();
        novaOs.setDataAbertura(dataAbertura);
        novaOs.setDataLimite(dataLimite);
        novaOs.setStatus(StatusOS.EM_ABERTO); // RN04: nasce sempre em aberto
        novaOs.setEquipamento(equipSelected);
        novaOs.setDescricaoDefeito(descricaoDefeito);
        novaOs.setCusto(custo);
        novaOs.setObservacoes(null); // observações são preenchidas depois pelo técnico (T3)
        novaOs.setCliente(clienteLocalizado);
        novaOs.setTecnico(null); // sem técnico até alguém assumir a OS na fila

        // 6. Envia para persistência no banco de dados
        boolean sucesso = osDao.salvarOS(novaOs);

        if (sucesso) {
            JOptionPane.showMessageDialog(telaAtual,
                    "Ordem de Serviço emitida com sucesso!\n" +
                            "Número da OS: " + novaOs.getNumOs() + "\n" +
                            "Prazo máximo de entrega (15 dias): "
                            + new java.text.SimpleDateFormat("dd/MM/yyyy").format(dataLimite),
                    "OS Gerada", JOptionPane.INFORMATION_MESSAGE);

            telaAtual.dispose();
        } else {
            lblMensagemErro.setText("Erro de conexão ao gravar a Ordem de Serviço no banco de dados.");
        }
    }

    // ---------------------------------------------------------------
    // T1: OS finalizadas, aguardando retirada do cliente
    // ---------------------------------------------------------------

    public List<OrdemServico> listarFinalizadasPendentesEntrega() {
        return osDao.listarFinalizadasPendentesEntrega();
    }

    public boolean confirmarEntregaAoCliente(int numOs, JFrame telaAtual) {
        boolean sucesso = osDao.marcarComoConcluida(numOs);
        if (sucesso) {
            JOptionPane.showMessageDialog(telaAtual,
                    "OS #" + numOs + " marcada como concluída (equipamento entregue).",
                    "Entrega registrada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(telaAtual,
                    "Não foi possível atualizar a OS no banco de dados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return sucesso;
    }

    // ---------------------------------------------------------------
    // T2: busca de OS em andamento por cliente
    // ---------------------------------------------------------------

    public List<OrdemServico> listarPendentesPorCliente(Cliente cliente) {
        if (cliente == null)
            return List.of();
        return osDao.listarPendentesPorCliente(cliente.getIdCliente());
    }

    // ---------------------------------------------------------------
    // T3: fluxo do técnico
    // ---------------------------------------------------------------

    public List<OrdemServico> listarPendentesPorTecnico(Usuario tecnico) {
        return osDao.listarPendentesPorTecnico(tecnico.getIdUsuario());
    }

    public List<OrdemServico> listarDisponiveisParaTecnico() {
        return osDao.listarDisponiveisParaTecnico();
    }

    /** O técnico escolhe uma OS da fila e passa a ser o responsável por ela. */
    public OrdemServico assumirOS(int numOs, Usuario tecnico) {
        boolean sucesso = osDao.assumirOS(numOs, tecnico.getIdUsuario());
        if (!sucesso) {
            return null;
        }
        // Recarrega para já trazer o técnico/status atualizados na tela de detalhe
        return osDao.buscarPorNumOs(numOs);
    }

    public boolean salvarObservacoes(int numOs, String observacoes) {
        return osDao.atualizarObservacoes(numOs, observacoes);
    }
    
    public boolean finalizarOS(int numOs, JFrame telaAtual) {
        boolean sucesso = osDao.finalizarOS(numOs);
        if (!sucesso) {
            JOptionPane.showMessageDialog(telaAtual,
                    "Não foi possível atualizar o status da OS #" + numOs + " no banco de dados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return sucesso;
    }
}

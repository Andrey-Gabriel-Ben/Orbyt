package com.senacsoluctions.controler;

import java.util.Calendar;
import java.util.Date;

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
import com.senacsoluctions.view.Utils;

public class OrdemServicoControler {

    private OrdemServicoDao osDao = new OrdemServicoDao();

    public void emitirOrdemServico(Cliente clienteLocalizado, JComboBox<Equipamento> cbEquipamento, JTextField txtCusto,
            JTextArea txtObservacoes, JLabel lblMensagemErro, JFrame telaAtual) {

        // Limpando estados de erro visuais anteriores
        cbEquipamento.putClientProperty("JComponent.outline", null);
        txtCusto.putClientProperty("JComponent.outline", null);
        lblMensagemErro.setText("");

        // 1. Verificação: Se o usuário não buscou/encontrou um cliente antes
        if (clienteLocalizado == null) {
            lblMensagemErro.setText("Por favor, localize um cliente válido por CPF primeiro!");
            return;
        }

        // 2. Verificação: Equipamento/Aparelho obrigatório (CORRIGIDO: Cast para o
        // Model Equipamento)
        Equipamento equipSelected = (Equipamento) cbEquipamento.getSelectedItem();
        if (equipSelected == null) {
            Utils.mostrarErro(lblMensagemErro, cbEquipamento,
                    "Selecione ou cadastre um equipamento para este cliente!");
            return;
        }

        // 3. Verificação e Conversão do Custo Estimado
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

        // Calculando os 15 dias de prazo máximo usando Calendar (Regra de Negócio do
        // Senac)
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataAbertura);
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date dataLimite = cal.getTime();

        // 4. Montagem do Objeto de Modelo
        OrdemServico novaOs = new OrdemServico();
        novaOs.setDataAbertura(dataAbertura);
        novaOs.setDataLimite(dataLimite);
        novaOs.setStatus("ABERTA"); // Regra de negócio: Nasce sempre em aberto
        novaOs.setEquipamento(equipSelected); // Associa o objeto Equipamento real
        novaOs.setCusto(custo);
        novaOs.setObservacoes(txtObservacoes.getText().trim());
        novaOs.setCliente(clienteLocalizado); // Associa o cliente achado
        novaOs.setTecnico(null); // Sem técnico no momento da abertura (será assumida depois)

        // 5. Envia para persistência no Supabase/Banco de Dados
        boolean sucesso = osDao.salvarOS(novaOs);

        if (sucesso) {
            JOptionPane.showMessageDialog(telaAtual,
                    "Ordem de Serviço emitida com sucesso!\n" +
                            "Número da OS: " + novaOs.getNumOs() + "\n" +
                            "Prazo máximo de entrega (15 dias): "
                            + new java.text.SimpleDateFormat("dd/MM/yyyy").format(dataLimite),
                    "OS Gerada", JOptionPane.INFORMATION_MESSAGE);

            telaAtual.dispose(); // Fecha a tela de cadastro ao finalizar com sucesso
        } else {
            lblMensagemErro.setText("Erro de conexão ao gravar a Ordem de Serviço no banco de dados.");
        }
    }
}
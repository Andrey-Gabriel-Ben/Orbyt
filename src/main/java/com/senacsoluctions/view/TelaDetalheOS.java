package com.senacsoluctions.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.senacsoluctions.controler.OrdemServicoControler;
import com.senacsoluctions.model.OrdemServico;
import com.senacsoluctions.model.StatusOS;
import com.senacsoluctions.model.Usuario;
import com.senacsoluctions.utils.GeradorRelatorioOS;

/**
 * Tela padrão de detalhe da OS, aberta a partir de qualquer caminho do técnico
 * (TelaOSPendentesTecnico ou TelaIniciarOS).
 *
 * Exibe todas as especificações da OS (somente leitura), permite ao técnico
 * atualizar as observações do atendimento e, quando a OS estiver EM_ANDAMENTO,
 * concluí-la (status → FINALIZADA) gerando o PDF de comprovante.
 */
public class TelaDetalheOS extends JFrame {

    private final OrdemServicoControler osControler = new OrdemServicoControler();
    private final SimpleDateFormat fmtData     = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat fmtDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final OrdemServico os;
    private final Usuario tecnicoLogado;

    private JTextArea txtObservacoes;
    private JButton btnSalvarObservacoes;
    private JButton btnConcluirOS;
    private JButton btnFechar;

    public TelaDetalheOS(OrdemServico os, Usuario tecnicoLogado) {
        this.os = os;
        this.tecnicoLogado = tecnicoLogado;

        setTitle("Orbyt - Detalhes da OS #" + os.getNumOs());
        setSize(600, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));

        // ---------------------------------------------------------------
        // Cabeçalho: Número da OS + badge de prioridade
        // ---------------------------------------------------------------
        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

        JLabel lblNumOs = new JLabel("Ordem de Serviço  #" + os.getNumOs(), SwingConstants.LEFT);
        lblNumOs.setFont(new Font("Segoe UI", Font.BOLD, 20));
        painelCabecalho.add(lblNumOs, BorderLayout.WEST);

        JLabel lblPrioridade = new JLabel(Utils.descricaoPrioridade(os.getDataLimite()), SwingConstants.RIGHT);
        lblPrioridade.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrioridade.setForeground(Utils.corPrioridade(os));
        painelCabecalho.add(lblPrioridade, BorderLayout.EAST);

        add(painelCabecalho, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        // Corpo: Grid de campos informativos (leitura) + campo editável
        // ---------------------------------------------------------------
        JPanel painelCorpo = new JPanel(new GridBagLayout());
        painelCorpo.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int linha = 0;

        linha = adicionarSeparador(painelCorpo, gbc, linha, "Dados do Cliente");
        linha = adicionarCampo(painelCorpo, gbc, linha, "Nome:", os.getCliente().getNome());
        linha = adicionarCampo(painelCorpo, gbc, linha, "CPF:", formatarCpf(os.getCliente().getCpf()));
        linha = adicionarCampo(painelCorpo, gbc, linha, "Telefone:", formatarTelefone(os.getCliente().getTelefone()));
        linha = adicionarCampo(painelCorpo, gbc, linha, "E-mail:", notNullOr(os.getCliente().getEmail(), "-"));

        linha = adicionarSeparador(painelCorpo, gbc, linha, "Equipamento");
        linha = adicionarCampo(painelCorpo, gbc, linha, "Tipo:", os.getEquipamento().getTipo());
        linha = adicionarCampo(painelCorpo, gbc, linha, "Marca / Modelo:",
                os.getEquipamento().getMarca() + " " + os.getEquipamento().getModelo());
        linha = adicionarCampo(painelCorpo, gbc, linha, "Nº de Série:",
                notNullOr(os.getEquipamento().getNumSerie(), "-"));

        linha = adicionarSeparador(painelCorpo, gbc, linha, "Dados da OS");
        linha = adicionarCampo(painelCorpo, gbc, linha, "Status:", os.getStatus());
        linha = adicionarCampo(painelCorpo, gbc, linha, "Data de Abertura:",
                fmtDataHora.format(os.getDataAbertura()));
        linha = adicionarCampo(painelCorpo, gbc, linha, "Prazo Limite:",
                fmtData.format(os.getDataLimite()));
        linha = adicionarCampo(painelCorpo, gbc, linha, "Custo Estimado:",
                String.format("R$ %.2f", os.getCusto()));
        linha = adicionarCampo(painelCorpo, gbc, linha, "Técnico Responsável:",
                os.getTecnico() != null ? os.getTecnico().getNome() : "Não atribuído");

        // Descrição do defeito (read-only com destaque)
        linha = adicionarSeparador(painelCorpo, gbc, linha, "Defeito Relatado pelo Cliente");

        JTextArea txtDefeito = new JTextArea(notNullOr(os.getDescricaoDefeito(), "Nenhuma descrição informada."));
        txtDefeito.setEditable(false);
        txtDefeito.setLineWrap(true);
        txtDefeito.setWrapStyleWord(true);
        txtDefeito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDefeito.setBackground(new Color(50, 50, 60));
        txtDefeito.setForeground(new Color(230, 230, 230));
        txtDefeito.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollDefeito = new JScrollPane(txtDefeito);
        scrollDefeito.setPreferredSize(new Dimension(500, 65));

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        painelCorpo.add(scrollDefeito, gbc);
        gbc.gridwidth = 1;
        linha++;

        // Observações do técnico (editável)
        linha = adicionarSeparador(painelCorpo, gbc, linha, "Observações do Técnico  (editável)");

        boolean osEmAndamento = StatusOS.EM_ANDAMENTO.equals(os.getStatus());

        txtObservacoes = new JTextArea(notNullOr(os.getObservacoes(), ""), 5, 40);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        txtObservacoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservacoes.setEditable(osEmAndamento);

        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        scrollObs.setPreferredSize(new Dimension(500, 95));

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        painelCorpo.add(scrollObs, gbc);

        add(new JScrollPane(painelCorpo), BorderLayout.CENTER);

        // ---------------------------------------------------------------
        // Rodapé: Botões de ação
        // ---------------------------------------------------------------
        JPanel painelRodape = new JPanel();
        painelRodape.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(100, 34));

        btnSalvarObservacoes = new JButton("Salvar Observações");
        btnSalvarObservacoes.setPreferredSize(new Dimension(180, 34));
        btnSalvarObservacoes.setBackground(new Color(30, 144, 255));
        btnSalvarObservacoes.setForeground(Color.WHITE);
        btnSalvarObservacoes.putClientProperty("JButton.buttonType", "roundRect");
        btnSalvarObservacoes.setEnabled(osEmAndamento);

        // Botão de concluir — verde, só ativo quando a OS está EM_ANDAMENTO
        btnConcluirOS = new JButton("Concluir OS  (Gerar Relatório)");
        btnConcluirOS.setPreferredSize(new Dimension(225, 34));
        btnConcluirOS.setBackground(new Color(46, 139, 87));
        btnConcluirOS.setForeground(Color.WHITE);
        btnConcluirOS.putClientProperty("JButton.buttonType", "roundRect");
        btnConcluirOS.setEnabled(osEmAndamento);
        btnConcluirOS.setToolTipText(osEmAndamento
                ? "Marca a OS como FINALIZADA e gera o comprovante em PDF"
                : "Esta OS não pode ser concluída — status atual: " + os.getStatus());

        painelRodape.add(btnFechar);
        painelRodape.add(btnSalvarObservacoes);
        painelRodape.add(btnConcluirOS);
        add(painelRodape, BorderLayout.SOUTH);

        // ---------------------------------------------------------------
        // Eventos
        // ---------------------------------------------------------------
        btnFechar.addActionListener(e -> dispose());
        btnSalvarObservacoes.addActionListener(e -> salvarObservacoes());
        btnConcluirOS.addActionListener(e -> concluirOS());
    }

    // ---------------------------------------------------------------
    // Lógica de persistência e geração do PDF
    // ---------------------------------------------------------------

    private void salvarObservacoes() {
        String novasObservacoes = txtObservacoes.getText().trim();
        boolean sucesso = osControler.salvarObservacoes(os.getNumOs(), novasObservacoes);

        if (sucesso) {
            os.setObservacoes(novasObservacoes);
            JOptionPane.showMessageDialog(this,
                    "Observações da OS #" + os.getNumOs() + " salvas com sucesso!",
                    "Salvo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar as observações. Tente novamente.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void concluirOS() {
        // 1. Confirmação
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar conclusão da OS #" + os.getNumOs() + "?\n"
                + "O status passará para FINALIZADA e um comprovante em PDF será gerado.",
                "Concluir OS", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        // 2. Salva observações atuais antes de finalizar (laudo vai para o PDF)
        String obsAtuais = txtObservacoes.getText().trim();
        if (!obsAtuais.equals(notNullOr(os.getObservacoes(), ""))) {
            osControler.salvarObservacoes(os.getNumOs(), obsAtuais);
            os.setObservacoes(obsAtuais);
        }

        // 3. Muda o status para FINALIZADA no banco
        Date dataFechamento = new Date();
        boolean statusAtualizado = osControler.finalizarOS(os.getNumOs(), this);
        if (!statusAtualizado) return; // controller já exibiu mensagem de erro

        os.setStatus(StatusOS.FINALIZADA);

        // 4. JFileChooser para o usuário escolher onde salvar o PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar comprovante em PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivo PDF (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("OS_" + os.getNumOs() + "_Comprovante.pdf"));

        int opcaoSalvar = fileChooser.showSaveDialog(this);
        if (opcaoSalvar != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "OS concluída com sucesso, mas o PDF não foi salvo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            desativarBotoes();
            return;
        }

        // Garante extensão .pdf
        String caminho = fileChooser.getSelectedFile().getAbsolutePath();
        if (!caminho.toLowerCase().endsWith(".pdf")) {
            caminho += ".pdf";
        }

        // 5. Gera o PDF
        try {
            File pdf = GeradorRelatorioOS.gerarPdf(os, dataFechamento, caminho);

            JOptionPane.showMessageDialog(this,
                    "OS #" + os.getNumOs() + " finalizada com sucesso!\n"
                    + "Comprovante salvo em:\n" + pdf.getAbsolutePath(),
                    "OS Concluída", JOptionPane.INFORMATION_MESSAGE);

            // 6. Abre no visualizador padrão do SO
            GeradorRelatorioOS.abrirNoVisualizador(pdf);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "OS finalizada no banco, mas ocorreu um erro ao gerar o PDF:\n" + ex.getMessage(),
                    "Erro ao gerar PDF", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // 7. Bloqueia edição — OS não está mais em andamento
        desativarBotoes();
    }

    private void desativarBotoes() {
        txtObservacoes.setEditable(false);
        btnSalvarObservacoes.setEnabled(false);
        btnConcluirOS.setEnabled(false);
        btnConcluirOS.setToolTipText("OS já finalizada.");
        setTitle("Orbyt - Detalhes da OS #" + os.getNumOs() + "  [FINALIZADA]");
    }

    // ---------------------------------------------------------------
    // Helpers de construção do layout
    // ---------------------------------------------------------------

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, String valor) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painel.add(lblRotulo, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painel.add(lblValor, gbc);
        return linha + 1;
    }

    private int adicionarSeparador(JPanel painel, GridBagConstraints gbc, int linha, String titulo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(linha == 0 ? 0 : 12, 4, 2, 4);

        JLabel lblSec = new JLabel(titulo.toUpperCase());
        lblSec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSec.setForeground(new Color(173, 216, 230));
        lblSec.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 120, 160)));
        painel.add(lblSec, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 4, 5, 4);
        return linha + 1;
    }

    // ---------------------------------------------------------------
    // Helpers de formatação
    // ---------------------------------------------------------------

    private static String notNullOr(String valor, String alternativa) {
        return (valor != null && !valor.isBlank()) ? valor : alternativa;
    }

    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf != null ? cpf : "-";
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private static String formatarTelefone(String tel) {
        if (tel == null) return "-";
        if (tel.length() == 11)
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7);
        return tel;
    }
}

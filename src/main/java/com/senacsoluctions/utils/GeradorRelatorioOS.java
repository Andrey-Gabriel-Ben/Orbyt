package com.senacsoluctions.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.senacsoluctions.model.OrdemServico;

public class GeradorRelatorioOS {

    // ---------------------------------------------------------------
    // Fontes 
    // ---------------------------------------------------------------
    private static final PDType1Font FONTE_BOLD    = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font FONTE_NORMAL  = PDType1Font.HELVETICA;
    private static final PDType1Font FONTE_OBLIQUE = PDType1Font.HELVETICA_OBLIQUE;

    // ---------------------------------------------------------------
    // Medidas da página A4 em pontos (1 pt = 1/72 in)
    // ---------------------------------------------------------------
    private static final float LARGURA  = PDRectangle.A4.getWidth();   // ~595 pt
    private static final float ALTURA   = PDRectangle.A4.getHeight();  // ~842 pt
    private static final float MARGEM_H = 45f; // margem horizontal
    private static final float AREA_W   = LARGURA - 2 * MARGEM_H;

    private static final SimpleDateFormat FMT_DATA      = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FMT_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // ---------------------------------------------------------------
    // Método principal
    // ---------------------------------------------------------------

    /**
     * @param os              Objeto OS já populado com cliente, equipamento e técnico.
     * @param dataFechamento  Momento em que o técnico clicou em "Concluir OS".
     * @param caminhoDestino  Caminho completo onde o PDF será salvo (ex: "C:/tmp/OS_42.pdf").
     * @return O arquivo PDF gerado.
     * @throws IOException  Se ocorrer falha ao criar/escrever o arquivo.
     */
    public static File gerarPdf(OrdemServico os, Date dataFechamento, String caminhoDestino) throws IOException {

        File arquivo = new File(caminhoDestino);

        try (PDDocument doc = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            doc.addPage(pagina);

            try (PDPageContentStream cs = new PDPageContentStream(doc, pagina)) {
                float y = ALTURA - 40; // cursor Y (desce a cada bloco)

                // -----------------------------------------------------------
                // CABEÇALHO
                // -----------------------------------------------------------
                y = desenharTextoCentralizado(cs, "ORDEM DE SERVIÇO", FONTE_BOLD, 20, y);
                y -= 4;
                y = desenharTextoCentralizado(cs, "SENAC Solutions  -  Comprovante de Encerramento", FONTE_NORMAL, 11, y);
                y -= 12;

                desenharLinhaHorizontal(cs, MARGEM_H, y, LARGURA - MARGEM_H, 1.5f);
                y -= 14;

                // Nº OS + Status na mesma linha
                String numOsStr = "Nº OS:  " + os.getNumOs();
                String statusStr = "Status:  FINALIZADA";
                escreverTexto(cs, numOsStr, FONTE_BOLD, 12, MARGEM_H, y);
                escreverTexto(cs, statusStr, FONTE_BOLD, 12, MARGEM_H + AREA_W / 2, y);
                y -= 20;

                // -----------------------------------------------------------
                // DADOS GERAIS E PRAZOS
                // -----------------------------------------------------------
                y = desenharCabecalhoSecao(cs, "Dados Gerais e Prazos", y);

                String dataAbertura  = os.getDataAbertura() != null ? FMT_DATA_HORA.format(os.getDataAbertura()) : "-";
                String dataFecha     = dataFechamento != null ? FMT_DATA.format(dataFechamento) : "-";
                String nomeTecnico   = os.getTecnico() != null ? os.getTecnico().getNome() : "Não atribuído";
                String custo         = String.format("R$ %.2f", os.getCusto());

                y = desenharLinhaDupla(cs, "Data de Abertura:", dataAbertura, "Data de Fechamento:", dataFecha, y);
                y = desenharLinhaDupla(cs, "Técnico Responsável:", nomeTecnico, "Custo Total (R$):", custo, y);
                y -= 8;

                // -----------------------------------------------------------
                // INFORMAÇÕES DO CLIENTE
                // -----------------------------------------------------------
                y = desenharCabecalhoSecao(cs, "Informações do Cliente", y);

                String nomeCliente = os.getCliente().getNome();
                String cpfCliente  = formatarCpf(os.getCliente().getCpf());
                String telCliente  = formatarTelefone(os.getCliente().getTelefone());
                String mailCliente = notNullOr(os.getCliente().getEmail(), "-");

                y = desenharLinhaDupla(cs, "Nome do Cliente:", nomeCliente, "CPF:", cpfCliente, y);
                y = desenharLinhaDupla(cs, "Telefone:", telCliente, "E-mail:", mailCliente, y);
                y -= 8;

                // -----------------------------------------------------------
                // ESPECIFICAÇÕES DO EQUIPAMENTO
                // -----------------------------------------------------------
                y = desenharCabecalhoSecao(cs, "Especificações do Equipamento", y);

                String tipoEquip   = os.getEquipamento().getTipo();
                String marcaModelo = os.getEquipamento().getMarca() + " / " + os.getEquipamento().getModelo();
                String numSerie    = notNullOr(os.getEquipamento().getNumSerie(), "Não informado");

                y = desenharLinhaDupla(cs, "Tipo/Aparelho:", tipoEquip, "Marca / Modelo:", marcaModelo, y);
                y = desenharLinhaSimplesValor(cs, "Número de Série:", numSerie, y);
                y -= 8;

                // -----------------------------------------------------------
                // DESCRIÇÃO DO DEFEITO
                // -----------------------------------------------------------
                y = desenharCabecalhoSecao(cs, "Descrição do Defeito Relatado", y);
                String descricao = notNullOr(os.getDescricaoDefeito(), "Não informada.");
                y = desenharBlocoTexto(cs, descricao, FONTE_NORMAL, 11, y, 80);
                y -= 8;

                // -----------------------------------------------------------
                // OBSERVAÇÕES TÉCNICAS / LAUDO
                // -----------------------------------------------------------
                y = desenharCabecalhoSecao(cs, "Observações Técnicas / Laudo de Conclusão", y);
                String obs = notNullOr(os.getObservacoes(), "Sem observações registradas.");
                y = desenharBlocoTexto(cs, obs, FONTE_NORMAL, 11, y, 100);
                y -= 24;

                // -----------------------------------------------------------
                // LINHA DE ASSINATURAS
                // -----------------------------------------------------------
                float xEsq = MARGEM_H + 20;
                float xDir = MARGEM_H + AREA_W / 2 + 20;
                float largAssinatura = AREA_W / 2 - 40;

                desenharLinhaHorizontal(cs, xEsq, y, xEsq + largAssinatura, 0.5f);
                desenharLinhaHorizontal(cs, xDir, y, xDir + largAssinatura, 0.5f);
                y -= 14;
                escreverTexto(cs, "Assinatura do Técnico / Responsável", FONTE_NORMAL, 9, xEsq, y);
                escreverTexto(cs, "Assinatura do Cliente (Retirada)", FONTE_NORMAL, 9, xDir, y);

                // -----------------------------------------------------------
                // RODAPÉ
                // -----------------------------------------------------------
                float yRodape = 28;
                desenharLinhaHorizontal(cs, MARGEM_H, yRodape + 12, LARGURA - MARGEM_H, 0.5f);
                escreverTexto(cs, "SENAC Solutions  -  Sistema de Gestão de OS", FONTE_OBLIQUE, 8, MARGEM_H, yRodape);
                escreverTextoAlinhado(cs, "Página 1 de 1", FONTE_OBLIQUE, 8, LARGURA - MARGEM_H, yRodape);
            }

            doc.save(arquivo);
        }

        return arquivo;
    }

    /**
     * Tenta abrir o PDF no visualizador padrão do sistema operacional.
     * Falha silenciosamente se o SO não suportar Desktop.
     */
    public static void abrirNoVisualizador(File pdf) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(pdf);
            } catch (IOException e) {
                System.err.println("Não foi possível abrir o PDF automaticamente: " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers de layout
    // ---------------------------------------------------------------

    /** Cabeçalho de seção: texto em negrito com linha abaixo e retorna Y atualizado. */
    private static float desenharCabecalhoSecao(PDPageContentStream cs, String titulo, float y) throws IOException {
        escreverTexto(cs, titulo, FONTE_BOLD, 11, MARGEM_H, y);
        y -= 4;
        desenharLinhaHorizontal(cs, MARGEM_H, y, LARGURA - MARGEM_H, 0.5f);
        y -= 14;
        return y;
    }

    /** Linha com dois pares rótulo:valor lado a lado (layout de duas colunas). */
    private static float desenharLinhaDupla(PDPageContentStream cs,
            String rot1, String val1, String rot2, String val2, float y) throws IOException {
        float xDir = MARGEM_H + AREA_W / 2;

        escreverTexto(cs, rot1, FONTE_BOLD, 10, MARGEM_H, y);
        escreverTexto(cs, val1, FONTE_NORMAL, 10, MARGEM_H + 110, y);
        escreverTexto(cs, rot2, FONTE_BOLD, 10, xDir, y);
        escreverTexto(cs, val2, FONTE_NORMAL, 10, xDir + 110, y);

        return y - 16;
    }

    /** Linha com rótulo à esquerda e valor logo após, ocupando a largura inteira. */
    private static float desenharLinhaSimplesValor(PDPageContentStream cs,
            String rotulo, String valor, float y) throws IOException {
        escreverTexto(cs, rotulo, FONTE_BOLD, 10, MARGEM_H, y);
        escreverTexto(cs, valor, FONTE_NORMAL, 10, MARGEM_H + 110, y);
        return y - 16;
    }

    /**
     * Escreve um bloco de texto com quebra de linha manual a cada ~charsLinha caracteres.
     * Retorna o Y após o bloco.
     */
    private static float desenharBlocoTexto(PDPageContentStream cs, String texto,
            PDType1Font fonte, float tamanho, float y, int charsLinha) throws IOException {
        if (texto == null || texto.isBlank()) {
            escreverTexto(cs, "-", fonte, tamanho, MARGEM_H, y);
            return y - 14;
        }

        String[] palavras = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();

        for (String palavra : palavras) {
            if (linhaAtual.length() + palavra.length() > charsLinha) {
                escreverTexto(cs, linhaAtual.toString().trim(), fonte, tamanho, MARGEM_H, y);
                y -= 13;
                linhaAtual = new StringBuilder();
            }
            linhaAtual.append(palavra).append(" ");
        }
        if (!linhaAtual.isEmpty()) {
            escreverTexto(cs, linhaAtual.toString().trim(), fonte, tamanho, MARGEM_H, y);
            y -= 13;
        }
        return y;
    }

    /** Escreve texto em posição absoluta. */
    private static void escreverTexto(PDPageContentStream cs, String texto,
            PDType1Font fonte, float tamanho, float x, float y) throws IOException {
        if (texto == null) texto = "";
        cs.beginText();
        cs.setFont(fonte, tamanho);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }

    /** Escreve texto alinhado à direita em relação ao x informado. */
    private static void escreverTextoAlinhado(PDPageContentStream cs, String texto,
            PDType1Font fonte, float tamanho, float xDireito, float y) throws IOException {
        float largTexto = fonte.getStringWidth(texto) / 1000 * tamanho;
        escreverTexto(cs, texto, fonte, tamanho, xDireito - largTexto, y);
    }

    /** Texto centralizado horizontalmente na página. */
    private static float desenharTextoCentralizado(PDPageContentStream cs, String texto,
            PDType1Font fonte, float tamanho, float y) throws IOException {
        float largTexto = fonte.getStringWidth(texto) / 1000 * tamanho;
        float x = (LARGURA - largTexto) / 2;
        escreverTexto(cs, texto, fonte, tamanho, x, y);
        return y - (tamanho + 4);
    }

    /** Linha horizontal entre dois pontos X com espessura definida. */
    private static void desenharLinhaHorizontal(PDPageContentStream cs,
            float x1, float y, float x2, float espessura) throws IOException {
        cs.setLineWidth(espessura);
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
    }

    // ---------------------------------------------------------------
    // Helpers de formatação de texto
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
        if (tel.length() == 11) {
            return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7);
        }
        return tel;
    }
}

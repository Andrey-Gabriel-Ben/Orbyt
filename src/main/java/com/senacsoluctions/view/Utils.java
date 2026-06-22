package com.senacsoluctions.view;

import java.awt.Color;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.senacsoluctions.model.OrdemServico;

public class Utils {

    public static void mostrarErro(JLabel lblErro, JComponent campo, String mensagem) {
        lblErro.setText(mensagem);

        if (campo != null) {
            // Aplica a borda vermelha elegante do FlatLaf
            campo.putClientProperty("JComponent.outline", "error");
            campo.requestFocus();
        }
    }

    public static void limparErros(JLabel lblErro, JComponent... campos) {
        lblErro.setText("");

        for (JComponent campo : campos) {
            if (campo != null) {
                campo.putClientProperty("JComponent.outline", null);
            }
        }
    }

    // --- RN03: Sistema de Cores de Prioridade (estilo Overcooked) ---
    // Verde:    11 a 15 dias restantes (baixa prioridade)
    // Amarelo:  6 a 10 dias restantes (média prioridade)
    // Vermelho: 0 a 5 dias restantes ou prazo já estourado (alta prioridade)

    private static final Color VERDE_BAIXA = new Color(60, 160, 90);
    private static final Color AMARELO_MEDIA = new Color(210, 170, 30);
    private static final Color VERMELHO_ALTA = new Color(190, 60, 60);
    private static final Color VERMELHO_ESTOURADO = new Color(140, 20, 20);

    public static Color corPrioridade(Date dataLimite) {
        long dias = diasRestantes(dataLimite);

        if (dias < 0) {
            return VERMELHO_ESTOURADO; // prazo já passou: destaque crítico
        } else if (dias <= 5) {
            return VERMELHO_ALTA;
        } else if (dias <= 10) {
            return AMARELO_MEDIA;
        } else {
            return VERDE_BAIXA;
        }
    }

    public static Color corPrioridade(OrdemServico os) {
        return corPrioridade(os.getDataLimite());
    }

    public static long diasRestantes(Date dataLimite) {
        java.util.Calendar hoje = java.util.Calendar.getInstance();
        hoje.set(java.util.Calendar.HOUR_OF_DAY, 0);
        hoje.set(java.util.Calendar.MINUTE, 0);
        hoje.set(java.util.Calendar.SECOND, 0);
        hoje.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Calendar limite = java.util.Calendar.getInstance();
        limite.setTime(dataLimite);
        limite.set(java.util.Calendar.HOUR_OF_DAY, 0);
        limite.set(java.util.Calendar.MINUTE, 0);
        limite.set(java.util.Calendar.SECOND, 0);
        limite.set(java.util.Calendar.MILLISECOND, 0);

        long diffMs = limite.getTimeInMillis() - hoje.getTimeInMillis();
        return diffMs / (1000 * 60 * 60 * 24);
    }

    public static String descricaoPrioridade(Date dataLimite) {
        long dias = diasRestantes(dataLimite);
        if (dias < 0) return "ATRASADA (" + Math.abs(dias) + " dia(s) de atraso)";
        if (dias <= 5) return "Alta prioridade (" + dias + " dia(s) restante(s))";
        if (dias <= 10) return "Média prioridade (" + dias + " dia(s) restante(s))";
        return "Baixa prioridade (" + dias + " dia(s) restante(s))";
    }
}

package com.senacsoluctions.view;

import javax.swing.JComponent;
import javax.swing.JLabel;

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
}

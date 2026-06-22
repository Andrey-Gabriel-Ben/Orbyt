package com.senacsoluctions;
 
/**
 * Classe base para todos os testes do projeto ORBYT.
 *
 * O bloco estático garante que a propriedade java.awt.headless=true
 * seja definida ANTES de qualquer classe AWT/Swing ser carregada pela JVM.
 * Isso permite criar JTextField, JLabel, JPasswordField etc. nos testes
 * sem precisar de um monitor físico (útil em pipelines CI/CD).
 *
 * Todos os *Test devem estender esta classe.
 */
public abstract class BaseTest {
 
    static {
        System.setProperty("java.awt.headless", "true");
    }
}
 
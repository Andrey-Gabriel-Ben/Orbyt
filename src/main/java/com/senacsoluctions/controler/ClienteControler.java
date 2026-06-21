package com.senacsoluctions.controler;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.senacsoluctions.dao.ClienteDao;
import com.senacsoluctions.model.Cliente;
import com.senacsoluctions.view.Utils;
; // Importa a utilitária

public class ClienteControler {

    private ClienteDao clienteDao = new ClienteDao();

    public void cadastrarCliente(JTextField txtNome, JTextField txtCpf, JTextField txtTelefone,
            JTextField txtEmail, JLabel lblMensagemErro, JFrame telaAtual) {

        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();

        // 1. LIMPA TUDO COM UMA ÚNICA LINHA (Passando os campos que deseja resetar)
        Utils.limparErros(lblMensagemErro, txtNome, txtCpf, txtTelefone);

        // 2. VALIDAÇÕES ULTRA COMPACTAS
        if (nome.isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtNome, "O nome do cliente é obrigatório!");
            return;
        }

        if (cpf.isEmpty() || cpf.equals("   .   .   -  ")) {
            Utils.mostrarErro(lblMensagemErro, txtCpf, "O CPF do cliente é obrigatório!");
            return;
        }

        if (!isCpfValido(cpf)) {
            Utils.mostrarErro(lblMensagemErro, txtCpf, "O CPF incerido é inválido.");
            return;
        }

        if (clienteDao.buscarPorCpf(apenasNumeros(cpf)) != null) {
            Utils.mostrarErro(lblMensagemErro, txtCpf, "Este CPF já pertence a um cliente cadastrado!");
            return;
        }

        if (telefone.isEmpty() || telefone.equals("(  )     -    ") || apenasNumeros(telefone).length() < 11) {
            Utils.mostrarErro(lblMensagemErro, txtTelefone, "O telefone de contato é obrigatório!");
            return;
        }

        if (email.isEmpty()) {
            Utils.mostrarErro(lblMensagemErro, txtEmail, "O email de contato é obrigatório!");
            return;
        }

        // --- SALVAMENTO NO BANCO ---
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setCpf(apenasNumeros(cpf));
        novoCliente.setTelefone(apenasNumeros(telefone));
        novoCliente.setEmail(email.isEmpty() ? null : email);

        if (clienteDao.salvarCliente(novoCliente)) {
            JOptionPane.showMessageDialog(telaAtual, "Cliente cadastrado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            telaAtual.dispose();
        } else {
            Utils.mostrarErro(lblMensagemErro, null, "Erro técnico ao conectar com o Supabase.");
        }
    }

    public static String apenasNumeros(String textoSujo) {
        if (textoSujo == null)
            return "";
        return textoSujo.replaceAll("[^0-9]", "");
    }


    public static boolean isCpfValido(String cpfSujo) {
        String numerosCPF = apenasNumeros(cpfSujo);

        // Valida tamanho e sequências repetidas (ex: 111.111.111-11)
        if (numerosCPF.length() != 11 || numerosCPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] cpfArray = new int[11];
            for (int i = 0; i < 11; i++) {
                cpfArray[i] = Character.getNumericValue(numerosCPF.charAt(i));
            }

            /*--- CÁLCULO DO PRIMEIRO DÍGITO ---*/
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += cpfArray[i] * peso--;
            }

            int verificador1 = 11 - (soma % 11);
            if (verificador1 > 9) {
                verificador1 = 0;
            }

            /* --- CÁLCULO DO SEGUNDO DÍGITO --- */
            soma = 0;
            int peso2 = 11;
            for (int i = 0; i < 10; i++) {
                soma += cpfArray[i] * peso2--;
            }

            int verificador2 = 11 - (soma % 11);
            if (verificador2 > 9) {
                verificador2 = 0;
            }

            /*--- VERIFICAÇÃO FINAL ---*/
            return (verificador1 == cpfArray[9] && verificador2 == cpfArray[10]);

        } catch (Exception e) {
            return false;
        }
    }


}
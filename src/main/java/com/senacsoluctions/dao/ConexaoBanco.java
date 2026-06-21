package com.senacsoluctions.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoBanco {

    // Método responsável por ler o arquivo .properties e carregar as configurações
    private static Properties carregarPropriedades() {
        Properties propriedades = new Properties();
        try (InputStream fs = ConexaoBanco.class.getClassLoader().getResourceAsStream("banco.properties")) {
            if (fs == null) {
                System.err.println("Erro: Não foi possível ler o arquivo banco.properties dentro de resources!");
                return propriedades;
            }
            propriedades.load(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propriedades;
    }

    // Método principal que seu projeto vai chamar toda vez que o DAO precisar falar
    // com o banco
    public static Connection getConexao() {
        try {
            Properties props = carregarPropriedades();
            String url = props.getProperty("db.url");
            String usuario = props.getProperty("db.user");
            String senha = props.getProperty("db.password");

            // Registra explicitamente o Driver do PostgreSQL (evita erros em algumas
            // versões do Java)
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver do PostgreSQL não encontrado no Maven (pom.xml)!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao tentar conectar ao banco de dados PostgreSQL!");
            e.printStackTrace();
            return null;
        }
    }

    // Método utilitário para fechar a conexão de forma limpa nas suas classes DAO
    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Tentando conectar ao PostgreSQL...");
        Connection conn = ConexaoBanco.getConexao();

        if (conn != null) {
            System.out.println("Sucesso! Conexão estabelecida perfeitamente.");
            ConexaoBanco.fecharConexao(conn);
            System.out.println("Conexão fechada com segurança.");
        } else {
            System.out.println("Falha na conexão. Verifique os erros acima.");
        }
    }
}
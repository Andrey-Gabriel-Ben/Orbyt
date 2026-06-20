-- =======================================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - SENAC SOLUTIONS
-- POSTGRESQL DDL (ENTREGA 3)
-- =======================================================================

-- 1. CRIAÇÃO DOS TIPOS ENUMERADOS (ENUMS)
CREATE TYPE cargo_usuario AS ENUM ('ATENDENTE', 'TECNICO');
CREATE TYPE status_os AS ENUM ('EM_ABERTO', 'ORÇAMENTO EM ANÁLISE', 'EM_ANDAMENTO', 'FINALIZADA');


-- 2. TABELA: CLIENTE
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(15) NOT NULL, 
    email VARCHAR(100) NOT NULL
);


-- 3. TABELA: EQUIPAMENTO
CREATE TABLE equipamento (
    id_equipamento SERIAL PRIMARY KEY, 
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    num_serie VARCHAR(50) NOT NULL UNIQUE,
    descricao_defeito TEXT NOT NULL,
    id_cliente INT NOT NULL,
    
    -- Restrição de Integridade Referencial: Se o cliente for deletado, impede ou limpa
    CONSTRAINT fk_equipamento_cliente 
        FOREIGN KEY (id_cliente) 
        REFERENCES cliente(id_cliente)
);


-- 4. TABELA: USUARIO
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,       -- Espaço adequado para hash criptografado 
    cargo cargo_usuario NOT NULL       -- Utiliza o ENUM criado anteriormente
);


-- 5. TABELA: ORDEM_SERVICO (OS)
CREATE TABLE ordem_servico (
    num_os SERIAL PRIMARY KEY,       
    data_abertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    data_limite DATE NOT NULL,       
    status status_os NOT NULL DEFAULT 'EM_ABERTO',
    custo DECIMAL(10,2),            
    observacoes TEXT,               
    id_cliente INT NOT NULL,         
    id_equipamento INT NOT NULL,     
    id_tecnico INT,                  

    -- Chaves Estrangeiras
    CONSTRAINT fk_os_cliente 
        FOREIGN KEY (id_cliente) 
        REFERENCES cliente(id_cliente),
        
    CONSTRAINT fk_os_equipamento 
        FOREIGN KEY (id_equipamento) 
        REFERENCES equipamento(id_equipamento),
        
    CONSTRAINT fk_os_tecnico 
        FOREIGN KEY (id_tecnico) 
        REFERENCES usuario(id_usuario)
);

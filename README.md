<div align="center">

<img src="orbyt/assets/logo/logo-orbyt.png" width="450"/>

# 🚀 ORBYT

### Sistema Inteligente de Gestão de Ordens de Serviço

<p>
Solução desenvolvida para gerenciamento de clientes, equipamentos, técnicos e ordens de serviço da empresa fictícia <b>Senac Solutions</b>.
</p>

<br>

<img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=for-the-badge" />
<img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
<img src="https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white" />
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" />

<br><br>

<img src="https://img.shields.io/github/last-commit/alexx-al3/Orbyt?style=flat-square" />
<img src="https://img.shields.io/github/repo-size/alexx-al3/Orbyt?style=flat-square" />
<img src="https://img.shields.io/github/languages/top/alexx-al3/Orbyt?style=flat-square" />

</div>

---

# 📖 Sobre o Projeto

O **ORBYT** é um sistema de gerenciamento de ordens de serviço desenvolvido para a empresa fictícia **Senac Solutions**, especializada em manutenção de computadores e equipamentos de informática.

O projeto surgiu da necessidade de substituir processos manuais realizados por planilhas e registros físicos, proporcionando maior organização, rastreabilidade e eficiência no controle operacional.

O sistema centraliza informações de clientes, equipamentos, técnicos e serviços executados, permitindo melhor acompanhamento dos atendimentos e aumento da produtividade.

---

# 🎯 Objetivos

- 📋 Centralizar o gerenciamento de ordens de serviço
- 👥 Organizar o cadastro de clientes
- 💻 Controlar equipamentos e diagnósticos
- 👨‍🔧 Gerenciar técnicos e atendimentos
- 📈 Melhorar a produtividade operacional
- 🔎 Facilitar consultas e acompanhamento dos serviços
- 📊 Gerar informações para tomada de decisão

---

# 🏛️ Arquitetura do Sistema

```text
┌─────────────────┐
│     Usuário     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Aplicação Java  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Supabase     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ PostgreSQL DB   │
└─────────────────┘
```

---

# ⚙️ Funcionalidades

## 👤 Gestão de Clientes

- Cadastro de clientes
- Atualização de informações
- Consulta de clientes
- Histórico de atendimentos

## 💻 Gestão de Equipamentos

- Cadastro de equipamentos
- Associação com clientes
- Registro de defeitos
- Registro de diagnósticos

## 🛠️ Gestão de Ordens de Serviço

- Abertura de OS
- Alteração de status
- Controle de execução
- Histórico de manutenção
- Consulta de serviços realizados

## 👨‍🔧 Gestão de Técnicos

- Cadastro de técnicos
- Distribuição de atendimentos
- Controle de produtividade
- Histórico de serviços realizados

---

# 📋 Regras de Negócio

- Todo cliente pode possuir vários equipamentos.
- Todo equipamento pertence a apenas um cliente.
- Uma Ordem de Serviço deve estar vinculada a um cliente.
- Uma Ordem de Serviço deve possuir um equipamento associado.
- Uma Ordem de Serviço deve possuir um status válido.
- Um técnico pode atender várias Ordens de Serviço.
- Uma Ordem de Serviço só poderá ser concluída após diagnóstico.
- Todo atendimento realizado deve permanecer registrado para consulta futura.

---

# 🗄️ Modelo de Dados

### Principais Entidades

- Cliente
- Equipamento
- Técnico
- Ordem de Serviço

### Relacionamentos

```text
Cliente
│
├── Equipamento
│
└── OrdemServico
       │
       └── Técnico
```

---

# 🏗️ Tecnologias Utilizadas

| Tecnologia | Utilização |
|------------|------------|
| Java 17 | Desenvolvimento da aplicação |
| Maven | Gerenciamento de dependências |
| PostgreSQL | Banco de dados |
| Supabase | Backend e integração |
| Git | Controle de versão |
| GitHub | Hospedagem do projeto |

---

# 🧰 Ferramentas Utilizadas

- IntelliJ IDEA
- PostgreSQL
- Supabase
- Git
- GitHub
- Draw.io
- Figma

---

# 📸 Demonstração

## Logo do Projeto

<img src="orbyt/assets/logo/logo-orbyt.png" width="300"/>

### Telas do Sistema

🚧 Em desenvolvimento

Adicione aqui futuramente:

- Dashboard
- Cadastro de Clientes
- Cadastro de Equipamentos
- Ordens de Serviço
- Relatórios

---

# 📂 Estrutura do Projeto

```text
ORBYT
│
├── README.md
│
├── orbyt
│   ├── assets
│   │   └── logo
│   │       └── logo-orbyt.png
│   │
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── senacsolutions
│                       ├── Main.java
│                       ├── model
│                       ├── repository
│                       ├── service
│                       └── controller
│
├── pom.xml
└── target
```

---

# 🚀 Como Executar

## Clonar o repositório

```bash
git clone https://github.com/alexx-al3/Orbyt.git
```

## Entrar na pasta

```bash
cd Orbyt
```

## Instalar dependências

```bash
mvn clean install
```

## Executar o projeto

```bash
mvn exec:java
```

---

# 🛣️ Roadmap

## Concluído

- [x] Levantamento de requisitos
- [x] Modelagem inicial do banco
- [x] Estruturação do projeto Java
- [x] Configuração do Supabase

## Em Desenvolvimento

- [ ] CRUD Clientes
- [ ] CRUD Equipamentos
- [ ] CRUD Técnicos
- [ ] CRUD Ordens de Serviço

## Futuras Implementações

- [ ] Dashboard gerencial
- [ ] Relatórios PDF
- [ ] Controle de permissões
- [ ] Sistema multiempresa
- [ ] Integração WhatsApp
- [ ] API REST
- [ ] Versão SaaS
- [ ] Painel Administrativo

---

# 📊 Status do Projeto

🚧 Projeto em desenvolvimento acadêmico.

Versão atual:

```text
v0.1.0-alpha
```

---

# 👨‍💻 Equipe de Desenvolvimento

<table>
<tr>

<td align="center">
<a href="https://github.com/alexx-al3">
<img src="https://github.com/alexx-al3.png" width="120px"/>
<br>
<b>Alex Alves</b>
</a>
<br>
Backend Developer
<br>
Modelagem de Banco de Dados
</td>

<td align="center">
<a href="https://github.com/Andrey-Gabriel-Ben">
<img src="https://github.com/Andrey-Gabriel-Ben.png" width="120px"/>
<br>
<b>Andrey Gabriel</b>
</a>
<br>
Backend Developer
<br>
Testes e Documentação
</td>

</tr>
</table>

---

# 🎓 Orientação

<table>
<tr>

<td align="center">
<a href="https://github.com/razevedocosta">
<img src="https://github.com/razevedocosta.png" width="140px"/>
<br>
<b>Prof. Rodrigo Azevedo</b>
</a>
<br>
Orientador do Projeto
</td>

</tr>
</table>

---

# 📚 Contexto Acadêmico

Projeto desenvolvido como atividade prática do curso de Desenvolvimento de Sistemas do SENAC.

O objetivo é aplicar conceitos de:

- Programação Orientada a Objetos
- Banco de Dados
- Modelagem de Sistemas
- Engenharia de Software
- Versionamento com Git
- Integração com Backend

---

# 📄 Licença

Este projeto possui finalidade acadêmica.

© 2026 Equipe ORBYT - Todos os direitos reservados.

---

<div align="center">

## ⭐ Gostou do projeto?

Deixe uma estrela no repositório e acompanhe sua evolução.

Desenvolvido com ❤️ pela Equipe ORBYT

</div>

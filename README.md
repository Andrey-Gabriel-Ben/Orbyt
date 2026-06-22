<div align="center">

<img src="assets/logo/logo-orbyt.png" width="500"/>

# 🚀 Orbyt - Sistema de Gestão de Ordens de Serviço

### Gestão inteligente de atendimentos técnicos

<br>

<img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=for-the-badge"/>

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>

<img src="https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white"/>

<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"/>

<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/>

<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/>

</div>

---

# 📌 Sobre o Projeto

O **Orbyt** é um sistema de gestão de ordens de serviço desenvolvido para a empresa fictícia **Senac Solutions**, especializada em manutenção de computadores e equipamentos de informática.

O sistema foi criado com o objetivo de modernizar e otimizar o processo de atendimento técnico, substituindo controles manuais realizados por planilhas e anotações físicas.

Com uma interface intuitiva e recursos voltados para produtividade, o sistema permite o gerenciamento eficiente de clientes, equipamentos, técnicos e ordens de serviço.

---

# 🎯 Objetivos

- Centralizar o gerenciamento das ordens de serviço.
- Melhorar a organização dos atendimentos técnicos.
- Reduzir erros causados por controles manuais.
- Facilitar o acompanhamento do status dos serviços.
- Garantir maior produtividade e controle operacional.

---

# ⚙️ Funcionalidades

### 👤 Gestão de Clientes

- Cadastro de clientes.
- Consulta e atualização de informações.
- Histórico de atendimentos.

### 💻 Gestão de Equipamentos

- Cadastro de equipamentos.
- Associação de equipamentos aos clientes.
- Registro de problemas identificados.

### 🛠️ Gestão de Ordens de Serviço

- Abertura de novas ordens.
- Atualização de status.
- Registro de diagnósticos.
- Controle de serviços executados.

### 👨‍🔧 Gestão de Técnicos

- Cadastro de técnicos.
- Atribuição de ordens de serviço.
- Controle de atendimentos realizados.

---

# 🛠️ Tecnologias Utilizadas

Tecnologia| Finalidade
Java 20| Desenvolvimento da aplicação
Swing| Interface gráfica
FlatLaf| Aparência moderna
PostgreSQL| Banco de dados relacional
Supabase| Hospedagem do banco
JDBC| Comunicação com banco
BCrypt| Criptografia de senhas
Maven| Gerenciamento de dependências
Git| Controle de versão
GitHub| Colaboração e hospedagem
---

# 📂 Estrutura do Projeto

ORBYT
│
├── .github
├── .vscode
├── Banco de dados
│   └── CriacaoBanco.sql
│
├── Documentacao
│
├── src
│   └── main
│       ├── java
│       │   └── com.senacsolutions
│       │       ├── controller
│       │       ├── dao
│       │       ├── model
│       │       ├── utils
│       │       └── view
│       │
│       └── resources
│           ├── Banco.properties.example
│           ├── logo-app.png
│           └── logo-orbyt.png
│
├── target
├── pom.xml
├── .gitignore
└── README.md
```
---

🏛️ Arquitetura

O projeto segue uma arquitetura baseada no padrão MVC (Model-View-Controller), promovendo organização, manutenção facilitada e separação de responsabilidades.

Camada| Responsabilidade
Model| Entidades e regras de negócio
View| Interface gráfica Swing
Controller| Controle do fluxo da aplicação
DAO| Persistência e acesso aos dados
Utils| Recursos utilitários compartilhados

---

🔐 Segurança

O sistema implementa mecanismos para garantir a integridade dos dados:

- Criptografia de senhas com BCrypt
- Controle de acesso por perfil
- Validação de CPF
- Restrições de integridade no banco de dados
- Controle de concorrência em Ordens de Serviço

---
---

# 🚀 Como Executar o Projeto

### Pré-requisitos

- Java 17 ou superior
- Maven
- PostgreSQL
- Conta no Supabase

### Clonando o repositório

```bash
git clone https://github.com/Andrey-Gabriel-Ben/Orbyt .git
```

### Entrando no diretório

```bash
cd orbyt
```

### Executando

```bash
mvn clean install
mvn exec:java
```

---

# 📊 Status do Projeto

🚧 Em desenvolvimento

Funcionalidades sendo implementadas de forma incremental seguindo os requisitos definidos para o projeto.

---
🌱 Estratégia de Versionamento

O projeto utiliza Git e GitHub para controle de versão, permitindo desenvolvimento colaborativo através de branches específicas para implementação de funcionalidades, correções e homologação.

# ---

👨‍💻 Equipe de Desenvolvimento

<table align="center">
<tr><td align="center"><a href="https://github.com/alexx-al3">
<img src="https://github.com/alexx-al3.png" width="140px">
</a><br><br>

<b>Alex Alves</b>

<br>Desenvolvedor Java • Fotógrafo • Produtor Audiovisual

<br><br>

<a href="https://github.com/alexx-al3">GitHub</a>

</td><td width="80"></td><td align="center"><a href="https://github.com/Andrey-Gabriel-Ben">
<img src="https://github.com/Andrey-Gabriel-Ben.png" width="140px">
</a><br><br>

<b>Andrey Gabriel</b>

<br>Desenvolvedor Java

<br><br>

<a href="https://github.com/Andrey-Gabriel-Ben">GitHub</a>

</td></tr>
</table>---

👨‍🏫 Orientação Acadêmica

<table align="center">
<tr>
<td align="center"><b>Prof. Rodrigo Costa</b>

<br>Professor Orientador do Projeto Integrador

<br><br>

<a href="https://github.com/razevedocosta">
GitHub
</a></td>
</tr>
</table>
---

# 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos como parte das atividades da empresa fictícia **Senac Solutions**.

---


<div align="center">

Desenvolvido com carinho pela equipe Orbyt

</div>

<div align="center">

<img src="assets/logo/logo-orbyt.png" width="300"/>

### Sistema Desktop de Gestão de Ordens de Serviço

<p>
Desenvolvido para otimizar o gerenciamento de atendimentos técnicos, equipamentos, clientes e ordens de serviço da empresa fictícia <b>Senac Solutions</b>.
</p>

<br>

<img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=for-the-badge" />
<img src="https://img.shields.io/badge/Java-20-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
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

O **ORBYT** é um sistema desktop de gerenciamento de Ordens de Serviço desenvolvido para a empresa fictícia **Senac Solutions**, especializada em manutenção de computadores e equipamentos de informática.

O sistema foi criado para substituir controles realizados em planilhas e registros manuais, centralizando todas as informações em uma única aplicação.

A proposta é proporcionar maior organização, rastreabilidade e eficiência no gerenciamento de clientes, equipamentos, técnicos e atendimentos.

---

# 🎯 Objetivos

- 📋 Gerenciar Ordens de Serviço de forma centralizada
- 👥 Controlar informações de clientes
- 💻 Registrar equipamentos e diagnósticos
- 👨‍🔧 Organizar atividades dos técnicos
- 📈 Melhorar a produtividade operacional
- 🔎 Facilitar consultas e acompanhamento dos serviços
- 📊 Gerar informações para apoio à tomada de decisão

---

# 🏛️ Arquitetura do Sistema

```text
┌───────────────┐
│    Usuário    │
└───────┬───────┘
        │
        ▼
┌────────────────────┐
│ Aplicação Java 20  │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│     Supabase       │
│ API + Persistência │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│    PostgreSQL      │
└────────────────────┘
```

---

# ⚙️ Funcionalidades

## 👤 Gestão de Clientes

- Cadastro de clientes
- Consulta de clientes
- Atualização de informações
- Histórico de atendimentos

## 💻 Gestão de Equipamentos

- Cadastro de equipamentos
- Associação com clientes
- Registro de defeitos
- Registro de diagnósticos
- Histórico de manutenção

## 🛠️ Gestão de Ordens de Serviço

- Abertura de Ordem de Serviço
- Alteração de status
- Acompanhamento do atendimento
- Registro de observações
- Histórico completo da manutenção

## 👨‍🔧 Gestão de Técnicos

- Cadastro de técnicos
- Associação de técnicos às OS
- Controle de atendimentos
- Histórico de serviços executados

---

# 📋 Regras de Negócio

- Todo cliente pode possuir vários equipamentos.
- Todo equipamento deve estar vinculado a um cliente.
- Toda Ordem de Serviço deve possuir um cliente associado.
- Toda Ordem de Serviço deve possuir um equipamento associado.
- Toda Ordem de Serviço deve possuir um status válido.
- Um técnico pode atender várias Ordens de Serviço.
- Uma Ordem de Serviço só poderá ser concluída após o registro do diagnóstico.
- Todos os atendimentos devem permanecer armazenados para consultas futuras.

---

# 🗄️ Modelo Conceitual

## Entidades Principais

- Cliente
- Equipamento
- Técnico
- Ordem de Serviço

## Relacionamentos

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

| Tecnologia | Finalidade |
|------------|------------|
| Java 20 | Desenvolvimento da aplicação |
| Maven | Gerenciamento de dependências |
| PostgreSQL | Banco de dados |
| Supabase | Persistência e integração |
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
│                       │
│                       ├── model
│                       │   ├── Cliente.java
│                       │   ├── Equipamento.java
│                       │   ├── Tecnico.java
│                       │   └── OrdemServico.java
│                       │
│                       ├── repository
│                       │
│                       ├── service
│                       │
│                       ├── controller
│                       │
│                       └── util
│
├── pom.xml
└── target
```

---

# 📸 Demonstração

## Logo Oficial

<img src="orbyt/assets/logo/logo-orbyt.png" width="300"/>

## Telas do Sistema

🚧 Em desenvolvimento

Futuras demonstrações:

- Dashboard Principal
- Cadastro de Clientes
- Cadastro de Equipamentos
- Gestão de Técnicos
- Gestão de Ordens de Serviço
- Relatórios

---

# 🚀 Como Executar

## Clonar o Repositório

```bash
git clone https://github.com/Andrey-Gabriel-Ben/Orbyt
```

## Acessar o Diretório

```bash
cd Orbyt
```

## Compilar o Projeto

```bash
mvn clean install
```

## Executar

```bash
mvn exec:java
```

---

# 🛣️ Roadmap

## Concluído

- [x] Levantamento de requisitos
- [x] Análise de negócio
- [x] Definição da arquitetura
- [x] Configuração do Supabase
- [x] Estruturação inicial do projeto

## Em Desenvolvimento

- [ ] Cadastro de Clientes
- [ ] Cadastro de Equipamentos
- [ ] Cadastro de Técnicos
- [ ] Cadastro de Ordens de Serviço
- [ ] Integração completa com banco de dados

## Futuras Implementações

- [ ] Dashboard gerencial
- [ ] Relatórios PDF
- [ ] Exportação de dados
- [ ] Controle de permissões
- [ ] Sistema multiempresa
- [ ] API REST
- [ ] Notificações automáticas
- [ ] Integração WhatsApp
- [ ] Painel administrativo
- [ ] Versão SaaS

---

# 📊 Status do Projeto

🚧 Projeto em desenvolvimento acadêmico.

**Versão Atual**

```text
v0.1.0-alpha
```

---

# 👨‍💻 Equipe de Desenvolvimento

<table>
<tr>

<td align="center">
<a href="https://github.com/alexx-al3">
<img src="https://github.com/alexx-al3.png" width="120px" alt="Alex Alves"/>
<br>
<b>Alex Alves</b>
</a>
<br>
Desenvolvedor FrontEnd
<br>

</td>

<td align="center">
<a href="https://github.com/Andrey-Gabriel-Ben">
<img src="https://github.com/Andrey-Gabriel-Ben.png" width="120px" alt="Andrey Gabriel"/>
<br>
<b>Andrey Gabriel</b>
</a>
<br>
Desenvolvedor Backend
<br>

</td>

</tr>
</table>

---

# 🎓 Orientação

<table>
<tr>

<td align="center">
<a href="https://github.com/razevedocosta">
<img src="https://github.com/razevedocosta.png" width="140px" alt="Prof Rodrigo Azevedo"/>
<br>
<b>Prof. Rodrigo Azevedo</b>
</a>
<br>
Orientador do Projeto
</td>

</tr>
</table>

---

# 📚 Competências Aplicadas

- Programação Orientada a Objetos (POO)
- Java 20
- Banco de Dados Relacional
- PostgreSQL
- Integração com Supabase
- Engenharia de Software
- Modelagem de Sistemas
- Arquitetura de Software
- Git e GitHub
- Desenvolvimento de Sistemas

---

# 🎯 Público-Alvo

Empresas de assistência técnica e manutenção de equipamentos que necessitam de um sistema para:

- Controle de clientes
- Registro de equipamentos
- Gestão de Ordens de Serviço
- Organização de atendimentos
- Histórico de manutenção

---

# 📄 Licença

Projeto desenvolvido para fins acadêmicos no curso de Desenvolvimento de Sistemas.

© 2026 Equipe ORBYT. Todos os direitos reservados.

---

<div align="center">

## ⭐ Gostou do projeto?

Deixe uma estrela no repositório para acompanhar sua evolução.

Desenvolvido com ❤️ pela Equipe ORBYT.

</div>

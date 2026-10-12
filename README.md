# 🏥 Gestão de Clínica Médica

Sistema web para gestão completa de clínicas médicas, controlando contas de acesso, pacientes, médicos, especialidades, agendas de atendimento, consultas e prontuários médicos.

O projeto é estruturado com backend em **Spring Boot (API RESTful)** integrado a banco de dados **MySQL**, e frontend desacoplado em **HTML5, CSS3 e JavaScript (Vanilla)** servido de forma estática pela aplicação.

---

## 📌 Sumário
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Pré-requisitos](#-pré-requisitos)
- [Passo a Passo para Executar](#-passo-a-passo-para-executar)
  - [1. Configurar o Banco de Dados](#1-configurar-o-banco-de-dados-mysql)
  - [2. Configurar as Credenciais no Projeto](#2-configurar-as-credenciais-applicationproperties)
  - [3. Iniciar a Aplicação](#3-iniciar-a-aplicação)
  - [4. Acessar o Sistema](#4-acessar-o-sistema)
  - [5. Criar o Primeiro Administrador (CLI)](#5-criar-o-primeiro-administrador-cli)
- [Perfis de Acesso e Funcionalidades](#-perfis-de-acesso-e-funcionalidades)
  - [Paciente](#-paciente)
  - [Médico](#-médico)
  - [Administrador (ADMIN)](#-administrador-admin)
- [Roteiro de Testes Recomendado](#-roteiro-de-testes-recomendado)
- [Documentação das Rotas da API REST](#-documentação-das-rotas-da-api-rest)
- [Segurança e Conformidade LGPD](#-segurança-e-conformidade-lgpd)

---

## 🚀 Tecnologias Utilizadas

- **Linguagem & Plataforma:** Java 21 LTS
- **Framework:** Spring Boot 4.x (Spring Web MVC, Spring Data JPA, Spring Security)
- **Banco de Dados:** MySQL 8.0+
- **Gerenciador de Dependências:** Apache Maven (com Maven Wrapper `mvnw` incluso)
- **Segurança:** BCrypt (Hash de senhas) e AES (Criptografia simétrica de dados sensíveis para LGPD)
- **Frontend:** HTML5, CSS3 responsivo e JavaScript moderno (Fetch API, SPA leve e modular)

---

## 📁 Estrutura do Projeto

```text
Gestao-de-Clinica-Medica/
├── src/main/java/com/clinicamedica/main/
│   ├── config/          Configurações de segurança, filtros de sessão e conversores JPA
│   ├── controller/      Controladores REST (@RestController) expondo as rotas /api
│   ├── dto/             Data Transfer Objects (Records) de entrada e saída
│   ├── model/           Entidades JPA e Enums (@Entity, @Table, @Enumerated)
│   ├── repository/      Interfaces Spring Data JPA (@Repository)
│   ├── service/         Regras de negócio, transações e validações de domínio
│   └── validation/      Classes auxiliares de validação de dados
│
├── src/main/resources/
│   ├── application.properties   Configurações de porta, conexão MySQL e sessões
│   └── static/                  Arquivos estáticos do frontend
│       ├── css/style.css        Estilização global
│       ├── js/                  Lógica cliente, comunicação com API e sessões
│       └── pages/               Telas do sistema (login, cadastro, dashboards)
│
├── Documentação/
│   ├── script-database.sql      Script de criação do banco de dados e dados iniciais
│   ├── script-criar-admin.md    Manual detalhado de criação do usuário administrador
│   └── *.pdf / *.png            Diagramas ER e documentações das sprints
│
├── pom.xml                      Descritor de dependências e plugins Maven
├── mvnw / mvnw.cmd              Maven Wrapper (Linux/Mac e Windows)
└── README.md                    Documentação principal do projeto
```

---

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado em seu computador:
1. **JDK 21** (Java Development Kit 21 ou superior):
   - Verifique com o comando: `java -version`
2. **MySQL Server** (versão 8.0 ou superior):
   - O serviço do MySQL deve estar em execução na porta padrão `3306`.
3. **Git** (opcional, para clonar o repositório).
4. Uma ferramenta visual para gerenciar o MySQL (recomendado: *MySQL Workbench*, *DBeaver* ou via linha de comando).

> **Nota:** Não é necessário ter o Maven instalado globalmente, pois o repositório inclui os scripts executáveis do Maven Wrapper (`mvnw` e `mvnw.cmd`).

---

## 🛠️ Passo a Passo para Executar

### 1. Configurar o Banco de Dados (MySQL)

1. Abra seu cliente MySQL (*MySQL Workbench*, *DBeaver* ou terminal).
2. Abra e execute o arquivo de script localizado em:
   ```text
   Documentação/script-database.sql
   ```
3. Este script irá:
   - Criar o banco de dados `clinica_medica`.
   - Criar todas as 8 tabelas relacionais (`usuario`, `paciente`, `medico`, `especialidade`, `medico_especialidade`, `agenda`, `consulta`, `atendimento`).
   - Inserir as 10 especialidades médicas iniciais (Clínica Geral, Cardiologia, Pediatria, etc.).

> ⚠️ **Atenção:** O script contém `DROP DATABASE IF EXISTS clinica_medica`. Caso execute-o novamente no futuro, ele recriará o banco do zero.

---

### 2. Configurar as Credenciais (`application.properties`)

Abra o arquivo [src/main/resources/application.properties](file:///c:/Users/Carollyne/Desktop/Gestao-de-Clinica-Medica/src/main/resources/application.properties) e verifique se as credenciais correspondem ao seu MySQL local:

```properties
spring.application.name=gestao-de-clinica-medica

# URL de conexão com o banco
spring.datasource.url=jdbc:mysql://localhost:3306/clinica_medica?useTimezone=true&serverTimezone=UTC

# Altere para o seu usuário e senha do MySQL:
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configurações JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none

# Segurança de Sessão HTTP
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict
server.servlet.session.cookie.secure=${SESSION_COOKIE_SECURE:false}
```

---

### 3. Iniciar a Aplicação

Abra o terminal (PowerShell no Windows, ou Bash no Linux/macOS) na pasta raiz do projeto:

**No Windows (PowerShell / Prompt):**
```powershell
.\mvnw.cmd spring-boot:run
```

**No Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Ou pela sua IDE favorita (IntelliJ IDEA, VS Code, Eclipse):**
- Importe o projeto como projeto Maven existente.
- Execute a classe principal `src/main/java/com/clinicamedica/main/GestaoDeClinicaMedicaApplication.java`.

Aguarde o Spring Boot inicializar até exibir a mensagem indicando que a aplicação subiu na porta **8080** (ex: `Tomcat started on port 8080 (http)`).

---

### 4. Acessar o Sistema

Abra o navegador e acesse:
👉 **[http://localhost:8080/](http://localhost:8080/)**

A rota raiz redireciona automaticamente para a tela de autenticação:
`http://localhost:8080/pages/login.html`

---

### 5. Criar o Primeiro Administrador (CLI)

Por motivos de segurança, o sistema **não permite cadastrar um Administrador pela interface pública**. O primeiro administrador é criado via terminal interativo.

1. Abra um terminal na pasta do projeto e gere o arquivo executável `.jar`:
   ```powershell
   .\mvnw.cmd -DskipTests package
   ```
2. Execute o comando com a flag `--criar-admin`:
   ```powershell
   java -jar target\gestao-de-clinica-medica-0.0.1-SNAPSHOT.jar --criar-admin
   ```
3. O terminal solicitará:
   - **Login:** defina o nome de usuário do admin (ex: `admin`).
   - **Senha:** digite a senha (de 15 a 72 caracteres UTF-8; a digitação fica oculta no console).
   - **Confirmação:** digite a mesma senha novamente.
4. Após a confirmação, o usuário `ADMIN` será salvo no banco de dados com hash BCrypt e o comando será finalizado.
5. Agora você já pode fazer login na aplicação com essa conta administradora!

---

## 👥 Perfis de Acesso e Funcionalidades

O sistema conta com Controle de Acesso Baseado em Perfis (RBAC), gerenciado por sessões autenticadas no servidor:

### 🧑‍🦱 Paciente
- **Cadastro e Autenticação:** Criação de conta pública (Login + Senha) vinculada aos dados do paciente (Nome, CPF, Data de Nascimento, Telefone, E-mail).
- **Agendamento de Consultas:** Escolhe uma Especialidade médica, seleciona um Médico habilitado e escolhe um horário disponível na agenda do médico.
- **Minhas Consultas:** Visualização das consultas agendadas, realizadas ou canceladas, com atualização dinâmica.
- **Cancelamento:** Pode cancelar uma consulta futura ainda no status `AGENDADO`. O horário volta automaticamente a ficar disponível na agenda do médico.
- **Prontuário e Histórico:** Consulta os laudos, diagnósticos e prescrições médicas de suas consultas já realizadas (somente leitura).

### 👨‍⚕️ Médico
- **Cadastro e Especialidades:** Cadastro público com CRM, telefone e vinculação a uma ou mais especialidades médicas cadastradas no sistema.
- **Gerenciamento de Agenda:** Disponibiliza novos horários de atendimento futuros (Data e Hora). Pode remover horários livres que ainda não foram reservados.
- **Atendimento e Prontuário:** Acessa as consultas marcadas do dia. Ao atender o paciente, preenche o prontuário (Diagnóstico obrigatório, Prescrição e Observações Clínicas), alterando o status da consulta para `REALIZADO`.
- **Cancelamento e Falta:** Pode cancelar uma consulta com aviso prévio ou registrar `NÃO COMPARECEU` após o horário estipulado.
- **Histórico Clínico:** Pesquisa e consulta o histórico dos atendimentos prestados pelo próprio médico.

### 🛡️ Administrador (ADMIN)
- **Gestão de Especialidades:** Cadastra, edita e remove especialidades médicas (uma especialidade vinculada a consultas existentes é protegida contra exclusão).
- **Gestão de Pacientes:** Pesquisa pacientes por nome, CPF, e-mail ou telefone e edita informações cadastrais.
- **Gestão de Médicos:** Pesquisa médicos por nome, CRM, telefone ou especialidade e atualiza seus dados.
- **Visão Geral:** Consulta registros e relatórios do sistema.

---

## 🧪 Roteiro de Testes Recomendado

Para testar o fluxo de ponta a ponta na sua máquina:

1. **Criar Admin:** Crie o admin via terminal (`--criar-admin`) e acesse o sistema para conferir a listagem de especialidades.
2. **Cadastrar Médico:** Na tela de cadastro, crie uma conta de perfil **Médico**, informe CRM, telefone e selecione especialidades (ex: Cardiologia e Clínica Geral).
3. **Disponibilizar Horário:** Faça login com o médico criado e adicione 2 horários futuros na agenda (ex: amanhã às 09:00 e às 10:00).
4. **Cadastrar Paciente:** Abra uma janela anônima (ou faça logout) e cadastre uma conta de perfil **Paciente** com dados fictícios.
5. **Agendar Consulta:** No painel do paciente, clique em *Agendar Consulta*, escolha a especialidade, o médico cadastrado e um dos horários disponíveis.
6. **Verificar a Reserva:** Volte ao painel do médico; a consulta agendada aparecerá na agenda do médico.
7. **Realizar Atendimento:** No painel do médico, clique em *Iniciar Atendimento*, preencha diagnóstico e receita e confirme.
8. **Conferir Prontuário:** Volte ao painel do paciente e veja o prontuário finalizado e disponível para leitura.

---

## 📡 Documentação das Rotas da API REST

Todas as requisições utilizam JSON no corpo e possuem o prefixo `/api`. As rotas protegidas validam a sessão ativa:

### 🔑 Autenticação e Sessão
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/usuarios/cadastrar` | Público | Cadastro inicial de usuário (Paciente ou Médico) |
| `POST` | `/api/usuarios/login` | Público | Autentica login/senha e cria a sessão HTTP |
| `GET` | `/api/usuarios/sessao` | Autenticado | Retorna dados e perfil da sessão ativa |
| `POST` | `/api/usuarios/logout` | Autenticado | Invalida a sessão atual |

### 👤 Pacientes
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/pacientes` | PACIENTE | Completa o perfil do paciente logado |
| `GET` | `/api/pacientes` | ADMIN | Lista pacientes cadastrados |
| `PUT` | `/api/pacientes/{id}` | ADMIN | Atualiza dados cadastrais do paciente |

### 🩺 Médicos
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/medicos` | MEDICO | Completa o perfil do médico e suas especialidades |
| `GET` | `/api/medicos` | Autenticado | Lista médicos e suas especialidades associadas |
| `GET` | `/api/medicos/{id}` | Autenticado | Busca médico específico por ID |
| `PUT` | `/api/medicos/{id}` | ADMIN | Atualiza dados do médico |
| `DELETE`| `/api/medicos/{id}` | ADMIN | Remove cadastro do médico |

### 🏷️ Especialidades
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/api/especialidades` | Público | Lista todas as especialidades cadastradas |
| `GET` | `/api/especialidades/{id}` | Público | Busca especialidade por ID |
| `POST` | `/api/especialidades` | ADMIN | Cadastra nova especialidade |
| `PUT` | `/api/especialidades/{id}` | ADMIN | Renomeia especialidade |
| `DELETE`| `/api/especialidades/{id}` | ADMIN | Exclui especialidade (se não tiver vínculos) |

### 📅 Agenda
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/agendas` | MEDICO | Cadastra horário livre na agenda do médico logado |
| `GET` | `/api/agendas/minhas` | MEDICO | Lista os horários da agenda do médico |
| `GET` | `/api/agendas/disponiveis` | Autenticado | Lista horários futuros livres (filtro opcional `?medicoId=`) |
| `DELETE`| `/api/agendas/{id}` | MEDICO | Remove horário livre da própria agenda |

### 📋 Consultas
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/consultas` | PACIENTE | Realiza agendamento vinculando agenda, médico e especialidade |
| `GET` | `/api/consultas/minhas` | PACIENTE | Lista as consultas do paciente logado |
| `GET` | `/api/consultas/medico/minhas` | MEDICO | Lista consultas da agenda do médico logado |
| `PUT` | `/api/consultas/{id}/cancelar` | PACIENTE | Cancela consulta agendada pelo paciente |
| `PUT` | `/api/consultas/{id}/cancelar-medico` | MEDICO | Cancela consulta pelo médico |
| `PUT` | `/api/consultas/{id}/nao-compareceu` | MEDICO | Registra falta do paciente após o horário previsto |
| `GET` | `/api/consultas` | ADMIN | Lista consultas para controle administrativo |

### 📝 Atendimentos (Prontuários)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/atendimentos` | MEDICO | Salva diagnóstico e prescrição, finalizando a consulta |
| `GET` | `/api/atendimentos/meus` | PACIENTE | Lista atendimentos/prontuários do paciente logado |
| `GET` | `/api/atendimentos/medico/meus` | MEDICO | Lista atendimentos realizados pelo médico logado |

---

## 🔒 Segurança e Conformidade LGPD

1. **Criptografia de Senhas:** Senhas são salvas no banco com hash unidirecional seguro utilizando **BCrypt** (`PasswordEncoder`).
2. **Criptografia de Dados Sensíveis (AES):** Campos sensíveis especificados pela LGPD são criptografados de forma transparente via JPA Converter (`CriptografiaAesConverter`):
   - CPF e Telefone do Paciente.
   - Telefone do Médico.
   - Diagnóstico, Prescrição e Observações Clínicas do Atendimento.
3. **Controle de Sessão e Cookies:**
   - Sessões gerenciadas via cookies seguros com atributos `HttpOnly` e `SameSite=Strict`.
   - O suporte a `Secure` pode ser habilitado em produção definindo a variável de ambiente `SESSION_COOKIE_SECURE=true`.
4. **Isolamento de Dados:** Cada paciente e médico acessa estritamente os seus próprios registros médicos através de validações no backend baseadas na sessão autenticada.

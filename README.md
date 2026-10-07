# Gestão de Clínica Médica

Aplicação web acadêmica para gerenciar contas, pacientes, médicos, especialidades, agendas, consultas e atendimentos. O backend usa Spring Boot e MySQL; as telas HTML, CSS e JavaScript ficam em `src/main/resources/static/pages`.

> **Ambiente de testes:** use dados fictícios. O armazenamento de demonstração no navegador não é prontuário persistente e não deve conter dados clínicos reais.

## O que está implementado

### Contas e acesso

- Cadastro público de pacientes e médicos. O cadastro público não permite criar um administrador.
- Login por sessão HTTP e encaminhamento para a dashboard correspondente ao perfil.
- A sessão é validada no servidor. As rotas da API negam acesso por padrão quando não há uma regra explícita.
- Senhas armazenadas com hash BCrypt. O primeiro administrador é criado por um comando de terminal descrito em [Documentação/script-criar-admin.md](Documentação/script-criar-admin.md).
- Cookies de sessão `HttpOnly` e `SameSite=Strict`. O suporte a cookie `Secure` pode ser ativado com `SESSION_COOKIE_SECURE=true` quando houver HTTPS.

### Dashboard administrativa

- Pesquisa de pacientes por nome, CPF, e-mail ou telefone.
- Pesquisa de médicos por nome, CRM, telefone ou especialidade.
- Edição administrativa do nome, telefone e e-mail do paciente e do nome, CRM e telefone do médico.
- Cadastro, renomeação e exclusão de especialidades. Uma especialidade vinculada a consultas não pode ser excluída.
- O perfil `ADMIN` não possui cadastro público.

### Agenda, consultas e prontuários

- O médico disponibiliza horários futuros para a própria agenda e pode remover um horário livre. Horários reservados não podem ser removidos.
- O paciente escolhe uma especialidade, um médico que a possua e um horário disponível definido pelo médico. A especialidade escolhida é salva na consulta e exibida nas dashboards do paciente e do médico.
- O backend verifica a disponibilidade do horário e se a especialidade pertence ao médico, evitando reservas duplicadas ou combinações inválidas.
- O paciente pode cancelar uma consulta futura ainda `AGENDADO`. A consulta continua no histórico, o horário é liberado e a tela orienta a fazer um novo agendamento se quiser reagendar.
- O médico pode cancelar uma consulta `AGENDADO`. Horários futuros são liberados; horários passados não voltam à disponibilidade. O médico pode marcar `NAO_COMPARECEU` depois do horário marcado.
- Só uma consulta `AGENDADO` pode receber um atendimento. O registro inclui diagnóstico obrigatório, prescrição e observações clínicas, e muda o status para `REALIZADO`. Consultas canceladas ou marcadas como não comparecimento não podem ser realizadas.
- O histórico do médico fica separado das consultas pendentes, é ordenado do mais recente para o mais antigo e permite busca pelo nome do paciente.
- Pacientes consultam os próprios prontuários em modo de leitura. Médicos consultam em modo de leitura os atendimentos da própria agenda. O servidor aplica esses filtros pela sessão autenticada.
- As dashboards do paciente e do médico consultam a API a cada 15 segundos enquanto a aba está visível e ao voltar para ela. A tela atualiza sem recarregar a página; pode haver até 15 segundos de atraso para mudanças feitas em outra sessão.

### Cadastro e validações

- As máscaras de CPF e telefone são apenas visuais: a API recebe somente números.
- Para testes, o CPF precisa conter 11 dígitos, mas a validação dos dígitos verificadores está temporariamente desativada. O CPF continua único no banco; use um número fictício que ainda não esteja cadastrado.
- Telefone precisa ter DDD e 10 ou 11 dígitos. Nome, e-mail, data de nascimento, login, senha, CRM, especialidade e campos clínicos também têm limites e validações no navegador e no backend.
- Os campos de diagnóstico, prescrição e observações clínicas usam o conversor AES existente. CPF e telefones também usam esse conversor nos modelos atuais.

## Tecnologias

- Java 21 e Spring Boot
- Spring MVC, Spring Data JPA e Spring Security
- Maven e MySQL
- BCrypt para hash de senhas
- HTML, CSS e JavaScript sem framework no front-end

## Estrutura do projeto

```text
src/main/java/com/clinicamedica/main/
  config/       Sessões, segurança, conversores e criação manual do admin
  controller/   Endpoints REST
  dto/          Contratos de entrada e saída da API
  model/        Entidades e enums
  repository/   Consultas Spring Data JPA
  service/      Regras de negócio e validações
  validation/   Validações compartilhadas

src/main/resources/
  application.properties
  static/
    pages/      Login, cadastro e dashboards
    js/         Chamadas à API, máscaras e lógica das telas
    css/        Estilos e regras responsivas

Documentação/
  script-database.sql
  migracao-especialidade-consulta.sql
  script-criar-admin.md
```

As páginas usadas pelo fluxo atual estão dentro de `static/pages`. O backend e o front-end devem ser servidos pela mesma aplicação Spring Boot.

## Configuração e inicialização

### Requisitos

- JDK 21
- MySQL
- Maven (ou o Maven Wrapper incluído no projeto)

### Banco de dados

Configure URL, usuário e senha do MySQL em `src/main/resources/application.properties`. O projeto usa `spring.jpa.hibernate.ddl-auto=none`; o Hibernate não cria nem atualiza tabelas automaticamente.

O script [Documentação/script-database.sql](Documentação/script-database.sql) cria o esquema e insere especialidades iniciais. **Ele começa com `DROP DATABASE IF EXISTS clinica_medica` e apaga o banco e todos os dados.** Execute-o apenas quando estiver criando a base do zero e puder perder o conteúdo atual.

Se a base já existe, preserve seus dados e execute uma única vez [Documentação/migracao-especialidade-consulta.sql](Documentação/migracao-especialidade-consulta.sql). Ela adiciona a coluna opcional `consulta.especialidade_id` e sua chave estrangeira, sem excluir tabelas ou consultas. Atendimentos antigos permanecem sem a especialidade escolhida e aparecem como “Especialidade não informada”.

### Iniciar a aplicação

No PowerShell, na pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Abra [http://localhost:8080/](http://localhost:8080/). A raiz encaminha para a tela de login. Para compilar sem iniciar o servidor:

```powershell
.\mvnw.cmd -DskipTests compile
```

## Criar o primeiro administrador

O administrador é criado fora do cadastro público. Gere o pacote e execute o comando em um terminal interativo:

```powershell
.\mvnw.cmd -DskipTests package
java -jar target\gestao-de-clinica-medica-0.0.1-SNAPSHOT.jar --criar-admin
```

O comando pede login e senha sem exibir a senha digitada. A senha precisa ter entre 15 e 72 bytes UTF-8 e ser confirmada. O usuário é salvo com BCrypt; se o login já existir, o comando não altera nem promove essa conta. O comando termina sem iniciar o servidor web.

Veja [Documentação/script-criar-admin.md](Documentação/script-criar-admin.md) para os detalhes.

## Fluxo para testar pela interface

1. Crie o banco do zero com o script SQL, ou aplique a migração indicada acima se quiser manter uma base existente.
2. Inicie o backend e crie o administrador pelo comando de terminal.
3. Entre como admin e confira as especialidades iniciais ou cadastre outras.
4. Cadastre um médico, selecione uma ou mais especialidades e entre na dashboard médica.
5. Na dashboard médica, disponibilize um horário futuro.
6. Cadastre um paciente usando dados fictícios. CPF de teste deve ter 11 dígitos e ser único na base.
7. Agende uma consulta escolhendo especialidade, médico e horário livre.
8. Confira a consulta e a especialidade nas dashboards do paciente e do médico. A reserva deve aparecer para ambas as contas.
9. Teste cancelamento pelo paciente ou pelo médico; confira o status e a disponibilidade do horário. Marque não comparecimento depois do horário da consulta.
10. Para uma consulta pendente, registre um atendimento. Confira o prontuário em modo de leitura nas dashboards do paciente e do médico.
11. Use o histórico do médico para pesquisar consultas anteriores e o admin para pesquisar ou editar cadastros.

As atualizações feitas por outra sessão aparecem automaticamente em até cerca de 15 segundos enquanto a dashboard estiver aberta e visível. Na demonstração local, o `localStorage` só é compartilhado pelo mesmo navegador/perfil e não substitui o banco de dados.

## Rotas da API

Todas as rotas usam prefixo `/api`. As rotas protegidas exigem a sessão criada no login, além do perfil indicado abaixo.

### Usuários

| Método e rota | Acesso | Descrição |
|---|---|---|
| `POST /api/usuarios/cadastrar` | Público | Criar conta de paciente ou médico. `ADMIN` é rejeitado. |
| `POST /api/usuarios/login` | Público | Autenticar e iniciar sessão. |
| `GET /api/usuarios/sessao` | Sessão autenticada | Consultar o perfil da sessão atual. |
| `POST /api/usuarios/logout` | Sessão autenticada | Encerrar a sessão. |

### Pacientes e médicos

| Método e rota | Acesso | Descrição |
|---|---|---|
| `POST /api/pacientes` | Paciente | Concluir o cadastro do próprio usuário. |
| `GET /api/pacientes` | Admin | Listar pacientes para pesquisa administrativa. |
| `PUT /api/pacientes/{id}` | Admin | Atualizar nome, telefone e e-mail. |
| `POST /api/medicos` | Médico | Concluir o próprio cadastro com CRM, telefone e `especialidadeIds`. |
| `GET /api/medicos` | Admin, médico ou paciente | Listar médicos e IDs de especialidades. |
| `GET /api/medicos/{id}` | Admin, médico ou paciente | Consultar um médico. |
| `PUT /api/medicos/{id}` | Admin | Atualizar nome, CRM e telefone. |
| `DELETE /api/medicos/{id}` | Admin | Excluir cadastro médico. |

### Especialidades

| Método e rota | Acesso | Descrição |
|---|---|---|
| `GET /api/especialidades` | Público | Listar especialidades para os formulários. |
| `GET /api/especialidades/{id}` | Público | Consultar uma especialidade. |
| `POST /api/especialidades` | Admin | Cadastrar especialidade. |
| `PUT /api/especialidades/{id}` | Admin | Renomear especialidade. |
| `DELETE /api/especialidades/{id}` | Admin | Excluir especialidade não vinculada a consultas. |

### Agenda

| Método e rota | Acesso | Descrição |
|---|---|---|
| `POST /api/agendas` | Médico | Criar horário futuro para a própria agenda (`dataAgenda`, `horario`). |
| `GET /api/agendas/minhas` | Médico | Listar os horários da própria agenda. |
| `GET /api/agendas/disponiveis?medicoId={id}` | Paciente ou admin | Listar horários futuros livres; `medicoId` é opcional. |
| `GET /api/agendas/{id}` | Admin | Consultar um horário pelo ID. |
| `DELETE /api/agendas/{id}` | Médico dono do horário | Excluir horário livre da própria agenda. Horários reservados não podem ser removidos. |

### Consultas

| Método e rota | Acesso | Descrição |
|---|---|---|
| `POST /api/consultas` | Paciente | Reservar horário enviando `agendaId`, `especialidadeId` e `observacoes` opcional. |
| `GET /api/consultas/minhas` | Paciente | Listar as próprias consultas. |
| `GET /api/consultas/medico/minhas` | Médico | Listar consultas da própria agenda. |
| `PUT /api/consultas/{id}/cancelar` | Paciente dono da consulta | Cancelar consulta futura ainda agendada. |
| `PUT /api/consultas/{id}/reagendar` | Paciente dono da consulta | Trocar para outro `agendaId` livre. A interface atual orienta cancelar e fazer um novo agendamento. |
| `PUT /api/consultas/{id}/cancelar-medico` | Médico da consulta | Cancelar consulta agendada. |
| `PUT /api/consultas/{id}/nao-compareceu` | Médico da consulta | Marcar falta após o horário agendado. |
| `GET /api/consultas` | Admin | Listar consultas. |
| `GET /api/consultas/data/{data}` | Admin | Listar por data ISO `AAAA-MM-DD`. |
| `GET /api/consultas/{id}` | Admin | Consultar pelo ID. |

### Atendimentos e prontuários

| Método e rota | Acesso | Descrição |
|---|---|---|
| `POST /api/atendimentos` | Médico dono da consulta | Registrar `consultaId`, diagnóstico obrigatório, prescrição e observações clínicas. |
| `GET /api/atendimentos/meus` | Paciente autenticado | Listar somente os próprios atendimentos. |
| `GET /api/atendimentos/medico/meus` | Médico autenticado | Listar somente os atendimentos da própria agenda. |

## Segurança e limitações conhecidas

- A configuração atual desativa CSRF para simplificar o uso da API local. Antes de disponibilizar o sistema publicamente, configure CSRF, HTTPS e `SESSION_COOKIE_SECURE=true`.
- A chave do conversor AES está no código e o `localStorage` é apenas demonstrativo. Não use estes recursos para dados reais sem revisão e configuração apropriadas.
- O CPF aceita qualquer sequência de 11 dígitos temporariamente, sem conferir os dígitos verificadores. A unicidade no banco continua ativa.
- A atualização automática usa consultas periódicas, não WebSocket: uma mudança externa pode levar até 15 segundos para aparecer.

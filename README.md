# Gestão de Clínica Médica

Aplicação web para cadastro e gestão de pacientes, médicos, especialidades e acessos da clínica. O projeto usa Spring Boot e MySQL no backend e páginas HTML/CSS/JavaScript em `src/main/resources/static`.

> **Estado do projeto:** cadastro, login por sessão, dashboard administrativa e gerenciamento básico de pacientes, médicos e especialidades usam a API e o banco. O fluxo de horários, reservas e atendimentos ainda é uma demonstração salva no navegador; não use esses dados clínicos como prontuário real.

## Tecnologias

- Java 21 e Spring Boot
- Spring MVC, Spring Data JPA e Spring Security
- Maven
- MySQL
- BCrypt para hash de senhas
- Conversor AES para campos sensíveis que já usam `CriptografiaAesConverter`
- HTML, CSS e JavaScript sem framework no front-end

## Estrutura principal

```text
src/main/java/com/clinicamedica/main/
  config/       Segurança, sessão, BCrypt e comando manual para criar admin
  controller/   Rotas REST
  dto/          Formatos de entrada e saída da API
  model/        Entidades e enums
  repository/   Acesso ao banco com Spring Data JPA
  service/      Regras de negócio

src/main/resources/
  application.properties
  static/
    pages/      Login, cadastro e dashboards definitivas
    js/         Chamadas à API, sessão e lógica das telas
    css/        Estilos

Documentação/
  script-database.sql
  criar-admin.md
```

As páginas definitivas ficam em `static/pages`. Existem arquivos HTML antigos fora dessa pasta; para usar a versão atual, abra o site pela raiz ou use os links das telas em `pages`.

## Configurar e iniciar

### 1. Preparar o MySQL

Crie o banco `clinica_medica` e configure URL, usuário e senha do seu MySQL em `src/main/resources/application.properties`. O Hibernate está configurado com `ddl-auto=none`, então as tabelas precisam existir antes de iniciar a aplicação.

O arquivo `Documentação/script-database.sql` contém o esquema das tabelas. **Atenção:** a primeira instrução é `DROP DATABASE clinica_medica`, que apaga o banco e os dados existentes antes de recriá-lo. Use esse script apenas em um banco novo ou depois de confirmar que pode perder os dados. Para preservar um banco existente, crie/aplique as tabelas sem executar essa instrução destrutiva.

### 2. Iniciar o backend e o site

No PowerShell, na pasta do projeto:

```powershell
mvn.cmd spring-boot:run
```

Abra [http://localhost:8080/](http://localhost:8080/). A raiz encaminha para a tela de login. O front-end chama a API na mesma origem; mantenha as páginas e a API servidas pelo Spring Boot.

Para apenas compilar o backend, sem executar testes:

```powershell
mvn.cmd -DskipTests compile
```

Os testes automatizados existentes podem ser executados com `mvn.cmd test`, mas o teste de contexto depende das configurações e da disponibilidade do banco.

## Criar o primeiro admin

O perfil `ADMIN` não aparece no cadastro público e não é criado durante a inicialização normal. Um operador com acesso ao servidor cria essa conta manualmente:

```powershell
mvn.cmd -DskipTests package
java -jar target/gestao-de-clinica-medica-0.0.1-SNAPSHOT.jar --criar-admin
```

Execute em um terminal interativo. O programa solicita login e senha, oculta a senha enquanto é digitada e pede confirmação. A senha deve ter de 15 a 72 bytes UTF-8. O comando cria a conta e termina sem iniciar o servidor web. Se o login já existir, não altera nem promove a conta existente.

A senha é salva com BCrypt, usando o mesmo `PasswordEncoder` do cadastro e do login. Inicie depois o sistema normalmente, sem `--criar-admin`, entre pelo login e a conta será direcionada à dashboard administrativa. Há instruções adicionais em [Documentação/criar-admin.md](Documentação/criar-admin.md).

## Como testar o fluxo pela interface

Use o mesmo navegador para trocar de conta; o armazenamento local usado pela demonstração de agenda não é compartilhado entre navegadores ou perfis privados.

1. Crie/inicie o banco e execute o comando de criação do admin acima.
2. Inicie o sistema normalmente e entre como admin.
3. Na dashboard administrativa, cadastre pelo menos uma especialidade.
4. Saia, cadastre um médico e conclua os dados do perfil. O cadastro inicia a sessão automaticamente e abre a dashboard médica.
5. Na dashboard médica, disponibilize uma data e horário futuros.
6. Saia, cadastre um paciente e escolha o horário disponibilizado. A reserva deve aparecer na área do paciente e na agenda do médico.
7. Entre novamente como médico, registre o atendimento de demonstração e confira o status atualizado.
8. Entre como admin para pesquisar pacientes/médicos e editar os campos administrativos disponíveis.

Para conferir a segurança dos perfis, tente abrir uma dashboard de outro perfil ou chamar uma rota protegida sem estar autenticado. O servidor deve negar a operação. O cadastro público também não deve aceitar `ADMIN` como perfil.

## Funcionalidades implementadas

### Login, sessão e perfis

- Cadastro público de contas `PACIENTE` e `MEDICO`; enviar `ADMIN` pela API pública é rejeitado.
- Login cria uma sessão HTTP no servidor; logout invalida essa sessão.
- A sessão identifica o usuário e o perfil no backend. A interface também redireciona para a dashboard correspondente.
- Cookie de sessão configurado como `HttpOnly` e `SameSite=Strict`. Em produção, use HTTPS e defina `SESSION_COOKIE_SECURE=true`.
- Rotas `/api/**` sem regra explícita são negadas por padrão.

### Pacientes e médicos

- Cadastro de paciente e médico usa os endpoints existentes e persiste os perfis no MySQL.
- Cada pessoa precisa concluir o cadastro com sessão do perfil correspondente, vinculada à própria conta.
- A dashboard admin pode listar e pesquisar pacientes e médicos.
- O admin pode editar nome, telefone e e-mail do paciente; e nome, CRM e telefone do médico. O login, CPF e data de nascimento não são editáveis por essa tela.
- Pacientes, médicos e especialidades possuem DTOs para entrada/saída da API.

### Especialidades e dashboard admin

- A dashboard administrativa fica em `pages/dashboard-admin.html` e exige perfil `ADMIN` validado pelo servidor.
- O admin pode cadastrar e renomear especialidades.
- A listagem de especialidades permanece pública para permitir que médicos selecionem suas áreas durante o cadastro; as alterações exigem perfil admin.

### Horários, reservas e atendimentos de demonstração

- O médico pode disponibilizar horários futuros pela dashboard médica.
- O paciente escolhe entre horários livres disponibilizados por médicos; a reserva marca o horário como indisponível para as próximas consultas à dashboard.
- A reserva aparece para o médico, que pode registrar diagnóstico, prescrição e observações de demonstração.
- Esses horários, reservas e atendimentos são gravados em `localStorage`, não no banco. Podem ser vistos em outras abas do mesmo navegador, mas não sincronizam entre dispositivos, não protegem contra reservas simultâneas em abas diferentes, não são protegidos como dados clínicos e podem ser apagados pelo navegador.
- A integração dessas funcionalidades com Agenda, Consulta e Atendimento no backend continua pendente.

## Rotas principais da API

| Método e rota | Acesso | Uso |
|---|---|---|
| `POST /api/usuarios/cadastrar` | Público; apenas Paciente/Médico | Criar usuário para cadastro |
| `POST /api/usuarios/login` | Público | Autenticar e criar sessão |
| `GET /api/usuarios/sessao` | Sessão autenticada | Consultar usuário/perfil da sessão |
| `POST /api/usuarios/logout` | Sessão autenticada | Encerrar sessão |
| `POST /api/pacientes` | Perfil Paciente | Concluir o cadastro da própria conta |
| `GET /api/pacientes` | Admin | Listar pacientes |
| `PUT /api/pacientes/{id}` | Admin | Editar nome, telefone e e-mail |
| `POST /api/medicos` | Perfil Médico | Concluir o cadastro da própria conta |
| `GET /api/medicos` e `GET /api/medicos/{id}` | Usuário autenticado | Consultar médicos |
| `PUT /api/medicos/{id}` e `DELETE /api/medicos/{id}` | Admin | Editar ou excluir cadastro médico |
| `GET /api/especialidades` e `GET /api/especialidades/{id}` | Público | Consultar especialidades |
| `POST /api/especialidades`, `PUT /api/especialidades/{id}`, `DELETE /api/especialidades/{id}` | Admin | Gerenciar especialidades |

## Segurança e dados

- Senhas são armazenadas como hash BCrypt; o hash não pode ser revertido para revelar a senha original.
- CPF e telefone de paciente e telefone de médico usam o conversor AES configurado nos modelos atuais.
- A dashboard admin recebe os dados necessários para pesquisa e edição dos cadastros. Restrinja o acesso à conta admin e use HTTPS quando disponibilizar o sistema.
- Não use o armazenamento local da demonstração para inserir informações reais de pacientes ou prontuários.

## Próximos passos

- Persistir Agenda, Consulta e Atendimento no backend e substituir os dados de demonstração do navegador.
- Definir as regras clínicas e os campos que médicos e pacientes podem consultar ou alterar.
- Adicionar paginação à pesquisa administrativa quando o volume de cadastros crescer.
- Planejar auditoria para alterações administrativas e proteção CSRF adequada ao ambiente de produção.

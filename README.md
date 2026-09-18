# Gestão de Clínica Médica

Um sistema desenvolvido em Java para auxiliar na gestão de clínicas médicas, controlando consultas, médicos, pacientes e usuários do sistema.

## 🚀 O que já foi feito até o momento (Status Atual)

O projeto está em fase de desenvolvimento. Abaixo está o resumo de tudo o que foi implementado nos últimos commits:

### 1. Estruturação do Projeto
- Projeto configurado com **Maven** (`pom.xml` ajustado com as dependências necessárias).
- Estrutura de pacotes organizada para execução em IDEs modernas (como VSCode, IntelliJ, etc).

### 2. Modelagem de Classes (Models)
- Criação das entidades principais do sistema:
  - `Consulta` e `StatusConsulta` (Enum)
  - `Medico`
  - `Paciente`
  - `Usuario` (com suporte a controle de perfil e autorização)

### 3. Banco de Dados
- Documentação e script de criação do banco de dados disponibilizados em `Documentação/script-database.txt`.
- Criação da classe `ConexaoBanco` para gerenciar a comunicação do sistema com o banco de dados **MySQL**.

### 4. Segurança e Criptografia
- Integração da biblioteca **BCrypt** para o *hashing* (criptografia) seguro das senhas dos usuários.
- Criação da classe utilitária `SegurancaHelper` para lidar com a encriptação e validação de senhas.

### 5. Acesso a Dados (DAO)
- Implementação da classe `UsuarioDAO`, que interage com o banco de dados para realizar operações de cadastro e login de usuários.

### 6. Testes Iniciais
- Criação de classes de teste para validar a base do sistema:
  - `TesteConexao`: Para verificar se o Java consegue se conectar corretamente ao banco MySQL.
  - `TesteUsuarioDAO`: Para validar a lógica de banco de dados e de login dos usuários.

---

## 🛠️ Tecnologias Utilizadas
- **Java** (versão 21)
- **Maven** (Gerenciamento de dependências)
- **MySQL** (Banco de dados)
- **BCrypt** (Segurança)
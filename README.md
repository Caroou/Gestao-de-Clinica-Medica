# 🏥 Gestão de Clínica Médica - API RESTful

Sistema de backend para auxiliar na gestão de clínicas médicas, controlando consultas, médicos, pacientes e usuários do sistema.

> **⚠️ Atenção:** O projeto passou por uma grande refatoração estrutural. Migramos da arquitetura MVC tradicional para uma **API RESTful** utilizando **Spring Boot**.

---

## 🚀 Tecnologias Utilizadas
* **Java 21**
* **Spring Boot** (Spring Web, Spring Data JPA, Spring Security)
* **Maven** (Gerenciamento de dependências)
* **MySQL** (Banco de dados relacional)
* **BCrypt & AES** (Criptografia e adequação à LGPD)

---

## 📁 Arquitetura em Camadas (Estrutura de Pastas)

A nossa arquitetura agora segue os padrões modernos de APIs RESTful:

* 📁 **`model`**: Nossas entidades e o mapeamento das tabelas do banco de dados (`@Entity`).
* 📁 **`repository`**: A camada de persistência e comunicação com o banco (Spring Data JPA).
* 📁 **`service`**: O "cérebro" da aplicação, contendo as regras de negócio e validações.
* 📁 **`controller`**: Os endpoints e rotas da API (`@GetMapping`, `@PostMapping`).
* 📁 **`config` / `security`**: Configurações globais, filtros e segurança da aplicação.
* 📁 **`dto`**: (*Data Transfer Object*) Estruturas para formatar e isolar os dados que entram e saem da API.

---

## ✅ Status Atual do Projeto

* CRUD de **Usuário**, **Paciente** já finalizados e funcionando.
* Página HTML simples disponibilizada no projeto para testes práticos de cadastro.

---

## 🛠️ Guia de Desenvolvimento: Próximos Passos
As próximas tabelas a serem implementadas são: **Médico**, **Agenda**, **Consulta** e **Atendimento**.  
Para evitar bugs (como loop infinito em JSON) e manter a padronização do código, a equipe deve seguir **rigorosamente** as 3 regras abaixo:

### 1. Ordem de Criação e Uso de DTOs
Sempre construa a funcionalidade de "baixo para cima":
1. `Repository` ➔ 2. `DTO` ➔ 3. `Service` ➔ 4. `Controller`.

🚨 **REGRA DE OURO:** Usem SEMPRE `record` para criar os DTOs de entrada e saída. **NUNCA** recebam ou devolvam a Entidade (Model) direto no Controller. Se fizerem isso, o Spring vai gerar um JSON em loop infinito devido às chaves estrangeiras.

### 2. Criptografia e LGPD 🔐
Para garantir a privacidade dos pacientes e a segurança do sistema:
* As senhas já utilizam **BCrypt** (hash irreversível).
* Dados sensíveis no banco utilizam criptografia **AES** automatizada.

Para aplicar a criptografia em novos campos sensíveis, basta colocar a anotação acima do atributo na classe Model:
```java
@Convert(converter = CriptografiaAesConverter.class)
private String campoSensivel;
```
* **Já aplicado em:** `Paciente` (CPF, Telefone).
* **Falta aplicar em:** `Medico` (Telefone) e `Atendimento` (Diagnóstico, Prescrição e Observações).

### 3. Liberação de Rotas (Spring Security)
O Spring Security bloqueia todas as rotas por padrão. Toda vez que você criar um Controller novo (ex: `/api/medicos`), você **deve** ir na classe `SecurityConfigurations` e adicionar a rota no método `.permitAll()` com os asteriscos.

* **Exemplo:** `"/api/medicos/**"`
* Se esquecer deste passo, o Spring retornará o Erro `403 (Acesso Negado)` ao tentar testar a rota no Postman ou no Navegador.

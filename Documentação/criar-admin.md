# Criar uma conta ADMIN manualmente

O admin não é criado automaticamente ao iniciar o sistema e não existe opção pública de cadastro com esse perfil. Para criar uma conta, um operador com acesso ao servidor executa explicitamente o comando `--criar-admin` em um terminal interativo.

## Windows PowerShell

Na pasta do projeto:

```powershell
mvn.cmd -DskipTests package
java -jar target/gestao-de-clinica-medica-0.0.1-SNAPSHOT.jar --criar-admin
```

O programa pede o login e a senha. A senha é digitada sem aparecer no terminal e precisa ter de 15 a 72 bytes UTF-8. Depois da confirmação, o programa cria o usuário como `ADMIN` e encerra sem iniciar o servidor web.

O valor salvo no banco é um hash BCrypt, feito pelo mesmo `PasswordEncoder` usado pelo cadastro e pelo login. Se o login já existir, o comando não altera nem promove a conta existente.

Para iniciar o sistema normalmente, use o comando habitual, sem `--criar-admin`. Não passe a senha como argumento, variável salva ou texto no código.

Em produção, sirva o sistema por HTTPS e defina `SESSION_COOKIE_SECURE=true` para que o navegador envie o cookie da sessão somente por conexão segura.

## Dashboard e acesso

Depois de criada, entre pelo login normal. Contas `ADMIN` são encaminhadas para `pages/dashboard-admin.html`, onde podem pesquisar pacientes e médicos, alterar seus dados básicos e cadastrar ou renomear especialidades. As rotas dessas operações verificam a sessão e o perfil no servidor.

Os cadastros públicos continuam restritos a pacientes e médicos. A conclusão desses cadastros exige uma sessão do perfil correspondente e só pode ser vinculada ao próprio usuário.

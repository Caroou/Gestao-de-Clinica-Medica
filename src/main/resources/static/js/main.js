// Variável global para guardar o ID do usuário após o passo 1
let idUsuarioRecemCriado = null;

// --- FUNÇÃO DE LOGIN ---
async function fazerLogin() {
    const divMsg = document.getElementById('msg_login');
    const jsonEnvio = {
        login: document.getElementById('log_login').value,
        senha: document.getElementById('log_senha').value
    };

    try {
        const resposta = await fetch('http://localhost:8080/api/usuarios/login', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(jsonEnvio)
        });
        if (resposta.ok) {
            const dados = await resposta.json();
            divMsg.className = 'mensagem sucesso';
            divMsg.innerText = '🔓 Bem-vindo(a)! Perfil: ' + dados.perfilUsuario;
        } else {
            divMsg.className = 'mensagem erro'; divMsg.innerText = '🚫 ' + await resposta.text();
        }
    } catch (e) { divMsg.className = 'mensagem erro'; divMsg.innerText = '❌ Falha na conexão.'; }
}

// --- FUNÇÃO DE CADASTRO PASSO 1 (USUÁRIO) ---
async function cadastrarUsuario() {
    const divMsg = document.getElementById('msg_cadastro');
    const perfilSelecionado = document.getElementById('cad_perfil').value;

    const jsonEnvio = {
        login: document.getElementById('cad_login').value,
        senha: document.getElementById('cad_senha').value,
        perfilUsuario: perfilSelecionado
    };

    try {
        const resposta = await fetch('http://localhost:8080/api/usuarios/cadastrar', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(jsonEnvio)
        });
        if (resposta.ok) {
            const dados = await resposta.json();
            idUsuarioRecemCriado = dados.id; // Salva o ID para usar no Passo 2

            // Esconde a tela de usuário e mostra a próxima tela com base no perfil
            document.getElementById('passo-1-usuario').classList.add('escondido');

            if(perfilSelecionado === 'PACIENTE') {
                document.getElementById('passo-2-paciente').classList.remove('escondido');
            } else if (perfilSelecionado === 'MEDICO') {
                document.getElementById('passo-2-medico').classList.remove('escondido');
            } else {
                divMsg.className = 'mensagem sucesso';
                divMsg.innerText = '✅ Admin criado! Não requer dados extras.';
            }
        } else {
            divMsg.className = 'mensagem erro'; divMsg.innerText = '❌ Erro: ' + await resposta.text();
        }
    } catch (e) { divMsg.className = 'mensagem erro'; divMsg.innerText = '❌ Falha na conexão.'; }
}

// --- FUNÇÃO DE CADASTRO PASSO 2 (PACIENTE) ---
async function cadastrarPaciente() {
    const divMsg = document.getElementById('msg_paciente');
    const jsonEnvio = {
        usuarioId: idUsuarioRecemCriado, // Pega o ID gerado automaticamente no passo 1
        nome: document.getElementById('pac_nome').value,
        cpf: document.getElementById('pac_cpf').value,
        dataNascimento: document.getElementById('pac_data').value,
        telefone: document.getElementById('pac_telefone').value,
        email: document.getElementById('pac_email').value
    };

    try {
        const resposta = await fetch('http://localhost:8080/api/pacientes', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(jsonEnvio)
        });
        if (resposta.ok) {
            divMsg.className = 'mensagem sucesso';
            divMsg.innerText = '✅ Paciente salvo! Redirecionando...';
            setTimeout(() => window.location.href = "index.html", 2000); // Manda pro login após 2 seg
        } else {
            divMsg.className = 'mensagem erro'; divMsg.innerText = '❌ ' + await resposta.text();
        }
    } catch (e) { divMsg.className = 'mensagem erro'; divMsg.innerText = '❌ Falha na conexão.'; }
}
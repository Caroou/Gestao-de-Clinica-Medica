/* =====================================================
   cadastro.js — Fluxo de cadastro multi-passo
   ===================================================== */

let idUsuarioCriado = null;

// Utilitários de UI
function showMsg(id, texto, tipo) {
  const el = document.getElementById(id);
  el.textContent = texto;
  el.className = `msg show ${tipo}`;
}
function hideMsg(id) {
  const el = document.getElementById(id);
  el.className = 'msg';
}
function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  btn.disabled = loading;
  btn.textContent = loading ? 'Aguarde...' : btn.dataset.label;
}

// Mostra o passo correto
function mostrarPasso(passo) {
  document.querySelectorAll('.step-pane').forEach(p => p.classList.add('hidden'));
  document.getElementById(passo).classList.remove('hidden');

  // Atualiza indicadores visuais
  if (passo === 'passo-usuario') {
    document.getElementById('ind-1').className = 'step active';
    document.getElementById('ind-2').className = 'step';
  } else {
    document.getElementById('ind-1').className = 'step done';
    document.getElementById('ind-2').className = 'step active';
  }
}

// ── PASSO 1: Cadastrar Usuário ──────────────────────
async function cadastrarUsuario() {
  hideMsg('msg-usuario');
  const login  = document.getElementById('login').value.trim();
  const senha  = document.getElementById('senha').value;
  const perfil = document.getElementById('perfil').value;

  if (!login || !senha) {
    showMsg('msg-usuario', 'Preencha login e senha.', 'erro');
    return;
  }

  setLoading('btn-usuario', true);

  try {
    const usuario = await UsuarioAPI.cadastrar({ login, senha, perfilUsuario: perfil });
    idUsuarioCriado = usuario.id;

    // Mostra o passo 2 correto de acordo com o perfil
    if (perfil === 'PACIENTE') {
      mostrarPasso('passo-paciente');
    } else if (perfil === 'MEDICO') {
      await carregarEspecialidades();
      mostrarPasso('passo-medico');
    }
  } catch (e) {
    showMsg('msg-usuario', '❌ ' + e.message, 'erro');
  } finally {
    setLoading('btn-usuario', false);
  }
}

// ── PASSO 2A: Cadastrar Paciente ────────────────────
async function cadastrarPaciente() {
  hideMsg('msg-paciente');
  const dados = {
    usuarioId:      idUsuarioCriado,
    nome:           document.getElementById('pac-nome').value.trim(),
    cpf:            document.getElementById('pac-cpf').value.trim(),
    dataNascimento: document.getElementById('pac-data').value,
    telefone:       document.getElementById('pac-telefone').value.trim(),
    email:          document.getElementById('pac-email').value.trim(),
  };

  if (!dados.nome || !dados.cpf || !dados.dataNascimento || !dados.telefone || !dados.email) {
    showMsg('msg-paciente', 'Preencha todos os campos.', 'erro');
    return;
  }

  setLoading('btn-paciente', true);
  try {
    await PacienteAPI.cadastrar(dados);
    showMsg('msg-paciente', '✅ Cadastro concluído! Redirecionando...', 'sucesso');
    setTimeout(() => window.location.href = '/pages/login.html', 2000);
  } catch (e) {
    showMsg('msg-paciente', '❌ ' + e.message, 'erro');
  } finally {
    setLoading('btn-paciente', false);
  }
}

// ── PASSO 2B: Cadastrar Médico ──────────────────────
async function carregarEspecialidades() {
  try {
    const lista = await EspecialidadeAPI.listar();
    const grid  = document.getElementById('grid-especialidades');
    grid.innerHTML = '';
    lista.forEach(esp => {
      grid.innerHTML += `
        <label>
          <input type="checkbox" name="esp" value="${esp.id}"> ${esp.nome}
        </label>`;
    });
  } catch {
    document.getElementById('grid-especialidades').innerHTML =
      '<p style="color:#64748b;font-size:.8rem">Não foi possível carregar especialidades.</p>';
  }
}

async function cadastrarMedico() {
  hideMsg('msg-medico');
  const selecionados = [...document.querySelectorAll('input[name="esp"]:checked')]
    .map(cb => parseInt(cb.value));

  if (selecionados.length === 0) {
    showMsg('msg-medico', 'Selecione pelo menos uma especialidade.', 'erro');
    return;
  }

  const dados = {
    usuarioId:       idUsuarioCriado,
    nome:            document.getElementById('med-nome').value.trim(),
    crm:             document.getElementById('med-crm').value.trim(),
    telefone:        document.getElementById('med-telefone').value.trim(),
    especialidadeIds: selecionados,
  };

  if (!dados.nome || !dados.crm || !dados.telefone) {
    showMsg('msg-medico', 'Preencha todos os campos.', 'erro');
    return;
  }

  setLoading('btn-medico', true);
  try {
    await MedicoAPI.cadastrar(dados);
    showMsg('msg-medico', '✅ Médico cadastrado! Redirecionando...', 'sucesso');
    setTimeout(() => window.location.href = '/pages/login.html', 2000);
  } catch (e) {
    showMsg('msg-medico', '❌ ' + e.message, 'erro');
  } finally {
    setLoading('btn-medico', false);
  }
}

// Inicia no passo 1
window.addEventListener('DOMContentLoaded', () => mostrarPasso('passo-usuario'));

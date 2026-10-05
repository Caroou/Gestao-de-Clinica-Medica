/* =====================================================
   auth.js — Login e controle de sessão (localStorage)
   ===================================================== */

// Guarda os dados do usuário logado no navegador
function salvarSessao(usuario) {
  localStorage.setItem('clinica_usuario', JSON.stringify(usuario));
}

function pegarSessao() {
  const raw = localStorage.getItem('clinica_usuario');
  if (!raw) return null;
  try { return JSON.parse(raw); } catch { limparSessao(); return null; }
}

function limparSessao() {
  localStorage.removeItem('clinica_usuario');
}

// Protege a página: se não tiver sessão, volta pro login
function exigirLogin() {
  const sessao = pegarSessao();
  if (!sessao) {
    window.location.href = '/pages/login.html';
    return null;
  }
  return sessao;
}

// Protege por perfil
function exigirPerfil(perfilEsperado) {
  const sessao = exigirLogin();
  if (!sessao) return null;
  if (sessao.perfilUsuario !== perfilEsperado) {
    alert('Acesso negado. Você não tem permissão para essa página.');
    window.location.href = '/pages/login.html';
    return null;
  }
  return sessao;
}

function destinoDoPerfil(perfil) {
  if (perfil === 'ADMIN') return '/pages/dashboard-admin.html';
  if (perfil === 'MEDICO') return '/pages/dashboard-medico.html';
  if (perfil === 'PACIENTE') return '/pages/dashboard-paciente.html';
  return '/pages/login.html';
}

async function exigirPerfilNoServidor(perfilEsperado) {
  const sessaoLocal = pegarSessao() || {};
  try {
    const sessaoServidor = await UsuarioAPI.sessao();
    const sessaoAtualizada = { ...sessaoLocal, ...sessaoServidor };
    salvarSessao(sessaoAtualizada);
    if (sessaoServidor.perfilUsuario !== perfilEsperado) {
      window.location.replace(destinoDoPerfil(sessaoServidor.perfilUsuario));
      return null;
    }
    return sessaoAtualizada;
  } catch {
    limparSessao();
    window.location.href = '/pages/login.html';
    return null;
  }
}

async function encerrarSessao() {
  try { await UsuarioAPI.logout(); } catch { /* remove a sessão local mesmo se a sessão do servidor expirou */ }
  limparSessao();
  window.location.href = '/pages/login.html';
}

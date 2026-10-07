/* =====================================================
   api.js — Centraliza todas as chamadas HTTP
   ===================================================== */
const API = '';

async function apiFetch(method, path, body = null) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin'
  };
  if (body) opts.body = JSON.stringify(body);

  let res;
  try {
    res = await fetch(API + path, opts);
  } catch {
    throw new Error('Não foi possível conectar à API. Confira se o backend está em execução.');
  }

  // Tenta parsear JSON; se não conseguir, retorna o texto puro como erro
  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!res.ok) {
    const detail = typeof data === 'string' ? data : (data.message || JSON.stringify(data));
    throw new Error(detail || `A solicitação falhou (${res.status}).`);
  }
  return data;
}

// --- Usuário ---
const UsuarioAPI = {
  cadastrar: (body) => apiFetch('POST', '/api/usuarios/cadastrar', body),
  login:     (body) => apiFetch('POST', '/api/usuarios/login', body),
  sessao:    ()     => apiFetch('GET',  '/api/usuarios/sessao'),
  logout:    ()     => apiFetch('POST', '/api/usuarios/logout'),
};

// --- Paciente ---
const PacienteAPI = {
  cadastrar: (body) => apiFetch('POST', '/api/pacientes', body),
  listar:    ()     => apiFetch('GET',  '/api/pacientes'),
  atualizar: (id, body) => apiFetch('PUT', `/api/pacientes/${id}`, body),
};

// --- Médico ---
const MedicoAPI = {
  cadastrar: (body) => apiFetch('POST', '/api/medicos', body),
  listar:    ()     => apiFetch('GET',  '/api/medicos'),
  atualizar: (id, body) => apiFetch('PUT', `/api/medicos/${id}`, body),
};

// --- Agenda ---
const AgendaAPI = {
  disponiveis: (medicoId) => apiFetch('GET', `/api/agendas/disponiveis${medicoId ? `?medicoId=${encodeURIComponent(medicoId)}` : ''}`),
  minhas: () => apiFetch('GET', '/api/agendas/minhas'),
  cadastrar: (body) => apiFetch('POST', '/api/agendas', body),
  excluir: (id) => apiFetch('DELETE', `/api/agendas/${id}`),
};

// --- Consultas ---
const ConsultaAPI = {
  agendar: (body) => apiFetch('POST', '/api/consultas', body),
  minhas: () => apiFetch('GET', '/api/consultas/minhas'),
  doMedico: () => apiFetch('GET', '/api/consultas/medico/minhas'),
  cancelar: (id) => apiFetch('PUT', `/api/consultas/${id}/cancelar`),
  cancelarPeloMedico: (id) => apiFetch('PUT', `/api/consultas/${id}/cancelar-medico`),
  marcarNaoCompareceu: (id) => apiFetch('PUT', `/api/consultas/${id}/nao-compareceu`),
  reagendar: (id, body) => apiFetch('PUT', `/api/consultas/${id}/reagendar`, body),
};

// --- Atendimento ---
const AtendimentoAPI = {
  registrar: (body) => apiFetch('POST', '/api/atendimentos', body),
  meus: () => apiFetch('GET', '/api/atendimentos/meus'),
  doMedico: () => apiFetch('GET', '/api/atendimentos/medico/meus'),
};

// --- Especialidade ---
const EspecialidadeAPI = {
  listar: () => apiFetch('GET', '/api/especialidades'),
  cadastrar: (body) => apiFetch('POST', '/api/especialidades', body),
  atualizar: (id, body) => apiFetch('PUT', `/api/especialidades/${id}`, body),
  excluir: (id) => apiFetch('DELETE', `/api/especialidades/${id}`),
};

let sessaoAdmin;
let pacientesAdmin = [];
let medicosAdmin = [];
let especialidadesAdmin = [];

window.addEventListener('DOMContentLoaded', async () => {
  ativarMascarasContato();
  sessaoAdmin = await exigirPerfilNoServidor('ADMIN');
  if (!sessaoAdmin) return;
  document.getElementById('nome-admin').textContent = `Admin: ${sessaoAdmin.login}`;
  document.getElementById('form-especialidade').addEventListener('submit', cadastrarEspecialidade);
  document.getElementById('form-edicao').addEventListener('submit', salvarEdicao);
  await carregarDados();
});

function escapar(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

function normalizar(texto) {
  return String(texto ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR');
}

async function carregarDados() {
  const [pacientes, medicos, especialidades] = await Promise.allSettled([
    PacienteAPI.listar(), MedicoAPI.listar(), EspecialidadeAPI.listar(),
  ]);
  if ([pacientes, medicos, especialidades].some(result => result.status === 'rejected')) {
    document.getElementById('tbody-pacientes').innerHTML = '<tr><td colspan="5">Não foi possível carregar os cadastros. Verifique sua sessão e tente novamente.</td></tr>';
    return;
  }
  pacientesAdmin = pacientes.value;
  medicosAdmin = medicos.value;
  especialidadesAdmin = especialidades.value;
  document.getElementById('total-pacientes').textContent = `(${pacientesAdmin.length})`;
  document.getElementById('total-medicos').textContent = `(${medicosAdmin.length})`;
  document.getElementById('total-especialidades').textContent = `(${especialidadesAdmin.length})`;
  renderPacientes();
  renderMedicos();
  renderEspecialidades();
}

function mostrarAba(nome) {
  document.querySelectorAll('.admin-tab').forEach(tab => tab.classList.add('hidden'));
  document.getElementById(`aba-${nome}`).classList.remove('hidden');
}

function renderPacientes() {
  const termo = normalizar(document.getElementById('busca-pacientes').value.trim());
  const lista = pacientesAdmin.filter(p => normalizar([p.nome, p.cpf, p.email, p.telefone].join(' ')).includes(termo));
  document.getElementById('tbody-pacientes').innerHTML = lista.length ? lista.map(p => `
    <tr><td>${escapar(p.nome)}</td><td>${escapar(p.cpf)}</td><td>${escapar(p.email)}</td><td>${escapar(p.telefone)}</td>
    <td style="text-align:right"><button class="btn btn-secondary btn-sm" onclick="abrirEdicao('PACIENTE', ${Number(p.id)})">Editar</button></td></tr>`).join('')
    : '<tr><td colspan="5" style="text-align:center;color:#64748b">Nenhum paciente encontrado.</td></tr>';
}

function renderMedicos() {
  const nomesEspecialidades = new Map(especialidadesAdmin.map(e => [Number(e.id), e.nome]));
  const termo = normalizar(document.getElementById('busca-medicos').value.trim());
  const lista = medicosAdmin.filter(m => {
    const nomes = (m.especialidadeIds || []).map(id => nomesEspecialidades.get(Number(id)) || '').join(' ');
    return normalizar([m.nome, m.crm, m.telefone, nomes].join(' ')).includes(termo);
  });
  document.getElementById('tbody-medicos').innerHTML = lista.length ? lista.map(m => {
    const nomes = (m.especialidadeIds || []).map(id => nomesEspecialidades.get(Number(id)) || '').filter(Boolean).join(', ');
    return `<tr><td>${escapar(m.nome)}</td><td>${escapar(m.crm)}</td><td>${escapar(m.telefone)}</td><td>${escapar(nomes || '—')}</td>
      <td style="text-align:right"><button class="btn btn-secondary btn-sm" onclick="abrirEdicao('MEDICO', ${Number(m.id)})">Editar</button></td></tr>`;
  }).join('') : '<tr><td colspan="5" style="text-align:center;color:#64748b">Nenhum médico encontrado.</td></tr>';
}

function renderEspecialidades() {
  const container = document.getElementById('lista-especialidades');
  container.innerHTML = especialidadesAdmin.length ? especialidadesAdmin.map(e => `
    <div class="consulta-card"><div class="info"><h4>${escapar(e.nome)}</h4></div>
      <button class="btn btn-secondary btn-sm" onclick="editarEspecialidade(${Number(e.id)})">Renomear</button></div>`).join('')
    : '<p style="color:#64748b">Nenhuma especialidade cadastrada.</p>';
}

async function cadastrarEspecialidade(event) {
  event.preventDefault();
  const msg = document.getElementById('msg-especialidade');
  try {
    await EspecialidadeAPI.cadastrar({ nome: document.getElementById('nova-especialidade').value.trim() });
    document.getElementById('nova-especialidade').value = '';
    msg.textContent = 'Especialidade cadastrada.';
    msg.className = 'msg show sucesso';
    await carregarDados();
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
  }
}

async function editarEspecialidade(id) {
  const especialidade = especialidadesAdmin.find(item => Number(item.id) === Number(id));
  if (!especialidade) return;
  const nome = window.prompt('Novo nome da especialidade:', especialidade.nome);
  if (nome == null || !nome.trim()) return;
  if (nome.trim().length > 100) { window.alert('O nome da especialidade pode ter até 100 caracteres.'); return; }
  try {
    await EspecialidadeAPI.atualizar(id, { id: Number(id), nome: nome.trim() });
    await carregarDados();
  } catch (error) {
    window.alert(error.message);
  }
}

function abrirEdicao(tipo, id) {
  const registro = tipo === 'PACIENTE'
    ? pacientesAdmin.find(item => Number(item.id) === Number(id))
    : medicosAdmin.find(item => Number(item.id) === Number(id));
  if (!registro) return;
  document.getElementById('edicao-tipo').value = tipo;
  document.getElementById('edicao-id').value = registro.id;
  document.getElementById('edicao-nome').value = registro.nome || '';
  document.getElementById('edicao-telefone').value = aplicarMascaraTelefone(registro.telefone || '');
  document.getElementById('edicao-identificador').value = tipo === 'MEDICO' ? registro.crm || '' : '';
  document.getElementById('edicao-email').value = tipo === 'PACIENTE' ? registro.email || '' : '';
  document.getElementById('titulo-edicao').textContent = tipo === 'PACIENTE' ? 'Editar paciente' : 'Editar médico';
  document.getElementById('label-edicao-identificador').textContent = 'CRM';
  document.getElementById('grupo-edicao-identificador').classList.toggle('hidden', tipo !== 'MEDICO');
  document.getElementById('grupo-edicao-email').classList.toggle('hidden', tipo !== 'PACIENTE');
  document.getElementById('edicao-identificador').required = tipo === 'MEDICO';
  document.getElementById('edicao-email').required = tipo === 'PACIENTE';
  document.getElementById('msg-edicao').className = 'msg';
  document.getElementById('modal-edicao').classList.remove('hidden');
}

function fecharEdicao() { document.getElementById('modal-edicao').classList.add('hidden'); }

async function salvarEdicao(event) {
  event.preventDefault();
  const id = Number(document.getElementById('edicao-id').value);
  const tipo = document.getElementById('edicao-tipo').value;
  const msg = document.getElementById('msg-edicao');
  const nome = document.getElementById('edicao-nome').value.trim();
  const telefone = somenteDigitos(document.getElementById('edicao-telefone').value);
  if (nome.length > 255 || !telefoneValido(telefone)) {
    msg.textContent = 'Informe um nome com até 255 caracteres e telefone com DDD.';
    msg.className = 'msg show erro';
    return;
  }
  try {
    if (tipo === 'PACIENTE') {
      const email = document.getElementById('edicao-email').value.trim();
      if (!document.getElementById('edicao-email').validity.valid || email.length > 255) throw new Error('Informe um e-mail válido com até 255 caracteres.');
      await PacienteAPI.atualizar(id, { nome, telefone, email });
    } else {
      const crm = document.getElementById('edicao-identificador').value.trim();
      if (!crm || crm.length > 50) throw new Error('Informe um CRM com até 50 caracteres.');
      await MedicoAPI.atualizar(id, { nome, telefone, crm });
    }
    fecharEdicao();
    await carregarDados();
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
  }
}

async function sair() { await encerrarSessao(); }

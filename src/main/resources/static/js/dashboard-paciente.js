let sessaoPaciente;
let medicosDisponiveis = [];

window.addEventListener('DOMContentLoaded', async () => {
  sessaoPaciente = await exigirPerfilNoServidor('PACIENTE');
  if (!sessaoPaciente) return;
  document.getElementById('nome-usuario').textContent = `Olá, ${sessaoPaciente.nomePaciente || sessaoPaciente.login}`;
  await carregarMedicos();
  renderConsultas();
});

window.addEventListener('storage', event => {
  if (['clinica_demo_consultas', 'clinica_demo_horarios'].includes(event.key)) {
    atualizarHorarios();
    renderConsultas();
  }
});

function escapar(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

function dataLegivel(data) {
  return new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(`${data}T12:00:00`));
}

function mesAbreviado(data) {
  return new Intl.DateTimeFormat('pt-BR', { month: 'short' }).format(new Date(`${data}T12:00:00`));
}

function renderConsultas() {
  const container = document.getElementById('lista-consultas');
  const consultas = ClinicaDemo.consultas()
    .filter(item => String(item.pacienteId) === String(sessaoPaciente.id))
    .sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  if (!consultas.length) {
    container.innerHTML = '<p style="color:#64748b;padding:1rem">Você ainda não tem consultas. Escolha um horário disponibilizado por um médico.</p>';
    return;
  }
  container.innerHTML = consultas.map(c => `
    <div class="consulta-card">
      <div class="date-badge"><div class="day">${escapar(c.data.slice(8, 10))}</div><div class="mon">${escapar(mesAbreviado(c.data))}</div></div>
      <div class="info"><h4>${escapar(c.medicoNome)}</h4><p>${escapar(dataLegivel(c.data))} às ${escapar(c.horario)} · Presencial</p></div>
      <span class="badge ${c.status === 'REALIZADO' ? 'badge-slate' : 'badge-green'}">${c.status === 'REALIZADO' ? 'Realizada' : 'Agendada'}</span>
    </div>`).join('');
}

function abrirModalAgendamento() {
  document.getElementById('msg-agendamento').className = 'msg';
  document.getElementById('modal-agendamento').classList.remove('hidden');
  atualizarHorarios();
}

function fecharModalAgendamento() {
  document.getElementById('modal-agendamento').classList.add('hidden');
  document.getElementById('select-horario').innerHTML = '';
  document.getElementById('div-horarios').classList.add('hidden');
  document.getElementById('obs-consulta').value = '';
  document.getElementById('msg-agendamento').className = 'msg';
}

async function carregarMedicos() {
  const select = document.getElementById('select-medico');
  const selectEspecialidade = document.getElementById('select-especialidade');
  const mensagemEspecialidades = document.getElementById('msg-especialidades');
  let apiMedicos = [];
  let apiEspecialidades = [];
  try { apiMedicos = await MedicoAPI.listar(); } catch { /* permite usar os médicos já guardados nesta demonstração */ }
  try { apiEspecialidades = await EspecialidadeAPI.listar(); } catch { /* indica abaixo que não foi possível carregar a lista */ }

  const mapa = new Map();
  [...ClinicaDemo.medicosLocais(), ...apiMedicos].forEach(m => {
    const medicoId = m.id;
    if (medicoId != null) mapa.set(String(medicoId), m);
  });
  medicosDisponiveis = [...mapa.values()];
  selectEspecialidade.innerHTML = '<option value="">-- Selecione uma especialidade --</option>' + apiEspecialidades.map(e =>
    `<option value="${escapar(e.id)}">${escapar(e.nome)}</option>`
  ).join('');
  mensagemEspecialidades.textContent = apiEspecialidades.length
    ? 'Escolha uma especialidade para ver os médicos disponíveis.'
    : 'Não foi possível carregar as especialidades. Verifique se há especialidades cadastradas e se a API está disponível.';
  select.disabled = true;
  select.innerHTML = '<option value="">-- Selecione primeiro a especialidade --</option>';
}

function filtrarMedicosPorEspecialidade() {
  const especialidadeId = document.getElementById('select-especialidade').value;
  const select = document.getElementById('select-medico');
  document.getElementById('div-horarios').classList.add('hidden');
  document.getElementById('select-horario').innerHTML = '';
  const filtrados = especialidadeId
    ? medicosDisponiveis.filter(m => (m.especialidadeIds || []).some(id => String(id) === String(especialidadeId)))
    : [];

  select.disabled = !especialidadeId;
  select.innerHTML = '<option value="">-- Selecione um médico --</option>' + filtrados.map(m =>
    `<option value="${escapar(m.id)}">${escapar(m.nome)}${m.crm ? ` (CRM: ${escapar(m.crm)})` : ''}</option>`
  ).join('');
  if (especialidadeId && !filtrados.length) {
    select.innerHTML += '<option value="" disabled>Nenhum médico cadastrado para esta especialidade</option>';
  }
}

function carregarHorarios() { atualizarHorarios(); }

function atualizarHorarios() {
  const medico = medicosDisponiveis.find(item => String(item.id) === String(document.getElementById('select-medico').value));
  const div = document.getElementById('div-horarios');
  const select = document.getElementById('select-horario');
  if (!medico) { div.classList.add('hidden'); select.innerHTML = ''; return; }
  const slots = ClinicaDemo.horarios().filter(slot => slot.disponivel && String(slot.medicoId) === String(medico.id))
    .sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  div.classList.remove('hidden');
  select.innerHTML = slots.length
    ? slots.map(slot => `<option value="${escapar(slot.id)}">${escapar(dataLegivel(slot.data))} às ${escapar(slot.horario)}</option>`).join('')
    : '<option value="">Este médico ainda não disponibilizou horários</option>';
}

function confirmarAgendamento() {
  const medico = medicosDisponiveis.find(item => String(item.id) === String(document.getElementById('select-medico').value));
  const slotId = document.getElementById('select-horario').value;
  const msg = document.getElementById('msg-agendamento');
  if (!medico) { msg.textContent = 'Selecione um médico.'; msg.className = 'msg show erro'; return; }
  if (!slotId) { msg.textContent = 'Este médico não tem horários disponíveis. Escolha outro médico ou tente mais tarde.'; msg.className = 'msg show erro'; return; }
  try {
    ClinicaDemo.agendarConsulta(slotId, {
      id: sessaoPaciente.id,
      nome: sessaoPaciente.nomePaciente || sessaoPaciente.login,
      observacoes: document.getElementById('obs-consulta').value.trim(),
    });
    renderConsultas();
    fecharModalAgendamento();
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
    atualizarHorarios();
  }
}

async function sair() { await encerrarSessao(); }

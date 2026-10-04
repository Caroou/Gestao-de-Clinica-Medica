let sessaoMedico;
let medicoAtual;
let consultaAtual;

window.addEventListener('DOMContentLoaded', async () => {
  sessaoMedico = await exigirPerfilNoServidor('MEDICO');
  if (!sessaoMedico) return;
  document.getElementById('nome-medico').textContent = sessaoMedico.nomeMedico || sessaoMedico.login;
  const campoData = document.getElementById('agenda-data');
  campoData.min = dataHojeLocal();
  await carregarMedico();
  renderAgenda();
  renderHorarios();
});

window.addEventListener('storage', event => {
  if (['clinica_demo_consultas', 'clinica_demo_horarios'].includes(event.key)) {
    renderAgenda();
    renderHorarios();
  }
});

function dataHojeLocal() {
  const data = new Date();
  data.setMinutes(data.getMinutes() - data.getTimezoneOffset());
  return data.toISOString().slice(0, 10);
}

function escapar(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

async function carregarMedico() {
  try {
    const medicos = await MedicoAPI.listar();
    medicoAtual = medicos.find(item => String(item.usuarioId) === String(sessaoMedico.id));
  } catch { /* o cadastro local permite continuar o fluxo de demonstração */ }
  if (!medicoAtual) {
    const local = ClinicaDemo.medicosLocais().find(item => String(item.usuarioId) === String(sessaoMedico.id));
    if (local) medicoAtual = local;
  }
  if (!medicoAtual && sessaoMedico.medicoId != null) medicoAtual = { id: sessaoMedico.medicoId, usuarioId: sessaoMedico.id, nome: sessaoMedico.nomeMedico || sessaoMedico.login };
}

function consultasDoMedico() {
  if (!medicoAtual) return [];
  return ClinicaDemo.consultas()
    .filter(item => String(item.medicoId) === String(medicoAtual.id) || String(item.medicoUsuarioId) === String(sessaoMedico.id))
    .sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
}

function renderAgenda() {
  const tbody = document.getElementById('tbody-agenda');
  const consultas = consultasDoMedico();
  document.getElementById('total-pacientes').textContent = `${consultas.length} Consulta(s)`;
  if (!consultas.length) {
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#64748b;padding:2rem">Nenhuma consulta agendada. Compartilhe os horários que disponibilizar com seus pacientes.</td></tr>';
    return;
  }
  tbody.innerHTML = consultas.map(item => `
    <tr>
      <td><strong>${escapar(item.horario)}</strong><br><small>${escapar(new Intl.DateTimeFormat('pt-BR').format(new Date(`${item.data}T12:00:00`)))}</small></td>
      <td><strong>${escapar(item.pacienteNome)}</strong><br><small style="color:#64748b">${item.observacoes ? escapar(item.observacoes) : 'Consulta'}</small></td>
      <td><span class="badge ${item.status === 'REALIZADO' ? 'badge-green' : 'badge-amber'}">${item.status === 'REALIZADO' ? 'Atendida' : 'Agendada'}</span></td>
      <td style="text-align:right">${item.status === 'REALIZADO' ? '<span class="badge badge-slate">Prontuário salvo</span>' : `<button class="btn btn-primary btn-sm" onclick="abrirProntuario('${escapar(item.id)}')">Registrar atendimento</button>`}</td>
    </tr>`).join('');
}

function renderHorarios() {
  const el = document.getElementById('lista-horarios');
  if (!el) return;
  if (!medicoAtual) {
    el.innerHTML = '<p style="color:#64748b;padding:1rem">Não foi possível vincular este usuário a um cadastro de médico.</p>';
    return;
  }
  const slots = ClinicaDemo.horarios().filter(item => String(item.medicoId) === String(medicoAtual.id))
    .sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  el.innerHTML = slots.length ? slots.map(slot => `
    <div class="consulta-card">
      <div class="info"><h4>${escapar(new Intl.DateTimeFormat('pt-BR').format(new Date(`${slot.data}T12:00:00`)))} às ${escapar(slot.horario)}</h4>
      <p>${slot.disponivel ? 'Disponível para agendamento' : 'Reservado por paciente'}</p></div>
      <span class="badge ${slot.disponivel ? 'badge-green' : 'badge-slate'}">${slot.disponivel ? 'Disponível' : 'Reservado'}</span>
    </div>`).join('') : '<p style="color:#64748b;padding:1rem">Nenhum horário disponibilizado ainda.</p>';
}

function abrirProntuario(id) {
  consultaAtual = consultasDoMedico().find(item => String(item.id) === String(id));
  if (!consultaAtual) return;
  document.getElementById('titulo-prontuario').textContent = `Atendimento – ${consultaAtual.pacienteNome}`;
  document.getElementById('diagnostico').value = '';
  document.getElementById('prescricao').value = '';
  document.getElementById('obs-clinica').value = '';
  document.getElementById('msg-prontuario').className = 'msg';
  document.getElementById('modal-prontuario').classList.remove('hidden');
}

function fecharProntuario() { document.getElementById('modal-prontuario').classList.add('hidden'); }

function salvarProntuario() {
  const msg = document.getElementById('msg-prontuario');
  const atendimento = {
    diagnostico: document.getElementById('diagnostico').value.trim(),
    prescricao: document.getElementById('prescricao').value.trim(),
    observacoesClinicas: document.getElementById('obs-clinica').value.trim(),
  };
  if (!atendimento.diagnostico) { msg.textContent = 'Preencha o diagnóstico.'; msg.className = 'msg show erro'; return; }
  try {
    ClinicaDemo.salvarAtendimento(consultaAtual.id, atendimento);
    renderAgenda();
    msg.textContent = 'Atendimento salvo neste navegador.';
    msg.className = 'msg show sucesso';
    setTimeout(fecharProntuario, 900);
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
  }
}

function abrirModalHorario() {
  document.getElementById('msg-horario').className = 'msg';
  document.getElementById('modal-horario').classList.remove('hidden');
}
function fecharModalHorario() { document.getElementById('modal-horario').classList.add('hidden'); }

function salvarHorario() {
  const data = document.getElementById('agenda-data').value;
  const hora = document.getElementById('agenda-hora').value;
  const msg = document.getElementById('msg-horario');
  if (!medicoAtual) { msg.textContent = 'O cadastro do médico não foi encontrado. Saia e entre novamente.'; msg.className = 'msg show erro'; return; }
  if (!data || !hora) { msg.textContent = 'Preencha data e horário.'; msg.className = 'msg show erro'; return; }
  if (new Date(`${data}T${hora}`) <= new Date()) { msg.textContent = 'Escolha um horário futuro.'; msg.className = 'msg show erro'; return; }
  try {
    ClinicaDemo.disponibilizarHorario({ medicoId: medicoAtual.id, medicoUsuarioId: sessaoMedico.id, medicoNome: medicoAtual.nome || sessaoMedico.nomeMedico || sessaoMedico.login, data, horario: hora });
    renderHorarios();
    msg.textContent = 'Horário disponibilizado para os pacientes.';
    msg.className = 'msg show sucesso';
    setTimeout(fecharModalHorario, 700);
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
  }
}

async function sair() { await encerrarSessao(); }

let sessaoPaciente;
let medicosDisponiveis = [];
let consultasPaciente = [];
let prontuariosPaciente = [];
let horariosExibidos = [];
let apiPacienteDisponivel = false;
let tokenCarregamentoHorario = 0;
let atualizacaoPacienteEmAndamento = false;

window.addEventListener('DOMContentLoaded', async () => {
  sessaoPaciente = await exigirPerfilNoServidor('PACIENTE');
  if (!sessaoPaciente) return;
  document.getElementById('nome-usuario').textContent = `Olá, ${sessaoPaciente.nomePaciente || sessaoPaciente.login}`;
  await carregarMedicos();
  await carregarConsultas();
  await carregarProntuarios();
  renderConsultas();
  iniciarAtualizacaoAutomaticaPaciente();
});

window.addEventListener('storage', event => {
  if (['clinica_demo_consultas', 'clinica_demo_horarios'].includes(event.key)) {
    if (!apiPacienteDisponivel) {
      carregarConsultas().then(renderConsultas);
      atualizarHorarios();
    }
  }
});

function iniciarAtualizacaoAutomaticaPaciente() {
  window.setInterval(atualizarDadosPacienteSemRecarregar, 15000);
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') atualizarDadosPacienteSemRecarregar();
  });
  window.addEventListener('focus', atualizarDadosPacienteSemRecarregar);
}

async function atualizarDadosPacienteSemRecarregar() {
  if (!apiPacienteDisponivel || atualizacaoPacienteEmAndamento || document.visibilityState === 'hidden') return;
  atualizacaoPacienteEmAndamento = true;
  try {
    const [consultasApi, prontuariosApi] = await Promise.all([
      ConsultaAPI.minhas(),
      AtendimentoAPI.meus(),
    ]);
    const consultasAtualizadas = consultasApi.map(dadosConsulta);
    const consultasMudaram = JSON.stringify(consultasAtualizadas) !== JSON.stringify(consultasPaciente);
    const prontuariosMudaram = JSON.stringify(prontuariosApi) !== JSON.stringify(prontuariosPaciente);
    if (consultasMudaram) consultasPaciente = consultasAtualizadas;
    if (prontuariosMudaram) prontuariosPaciente = prontuariosApi;
    if (consultasMudaram || prontuariosMudaram) renderConsultas();

    if (!document.getElementById('modal-agendamento').classList.contains('hidden')) {
      await atualizarHorarios();
    }
  } catch { /* preserva os dados já exibidos quando a API fica indisponível por alguns instantes */ }
  finally { atualizacaoPacienteEmAndamento = false; }
}

function escapar(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

function dataLegivel(data) {
  return new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(`${data}T12:00:00`));
}

function mesAbreviado(data) {
  return new Intl.DateTimeFormat('pt-BR', { month: 'short' }).format(new Date(`${data}T12:00:00`));
}

function dadosConsulta(item) {
  if (item.dataHora) return {
    ...item,
    data: item.dataHora.slice(0, 10),
    horario: item.dataHora.slice(11, 16),
    medicoNome: item.nomeMedico,
    pacienteNome: item.nomePaciente,
  };
  return item;
}

async function carregarConsultas() {
  if (apiPacienteDisponivel) {
    try {
      consultasPaciente = (await ConsultaAPI.minhas()).map(dadosConsulta);
      return;
    } catch { /* mantém o modo demonstrativo se o endpoint ainda não estiver disponível */ }
  }
  consultasPaciente = ClinicaDemo.consultas()
    .filter(item => String(item.pacienteId) === String(sessaoPaciente.id))
    .map(dadosConsulta);
}

async function carregarProntuarios() {
  if (apiPacienteDisponivel) {
    try {
      prontuariosPaciente = await AtendimentoAPI.meus();
      return;
    } catch { /* usa o registro local de demonstração se a API não responder */ }
  }
  prontuariosPaciente = ClinicaDemo.consultas()
    .filter(item => String(item.pacienteId) === String(sessaoPaciente.id) && item.status === 'REALIZADO' && item.atendimento)
    .map(item => ({
      consultaId: item.id,
      dataHora: item.atendimento.dataHora || item.atendimento.dataRegistro,
      diagnostico: item.atendimento.diagnostico,
      prescricao: item.atendimento.prescricao,
      observacoes: item.atendimento.observacoes || item.atendimento.observacoesClinicas,
    }));
}

function renderConsultas() {
  const container = document.getElementById('lista-consultas');
  const consultas = [...consultasPaciente]
    .sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  if (!consultas.length) {
    container.innerHTML = '<p style="color:#64748b;padding:1rem">Você ainda não tem consultas. Escolha um horário disponibilizado por um médico.</p>';
    return;
  }
  const prontuarios = apiPacienteDisponivel
    ? prontuariosPaciente
    : ClinicaDemo.consultas()
      .filter(item => String(item.pacienteId) === String(sessaoPaciente.id) && item.status === 'REALIZADO' && item.atendimento)
      .map(item => ({ consultaId: item.id }));
  container.innerHTML = consultas.map(c => {
    const cancelada = c.status === 'CANCELADO' || c.status === 'CANCELADA';
    const realizada = c.status === 'REALIZADO';
    const naoCompareceu = c.status === 'NAO_COMPARECEU';
    const labelStatus = cancelada ? 'Cancelada' : (naoCompareceu ? 'Não compareceu' : (realizada ? 'Realizada' : 'Agendada'));
    const badge = naoCompareceu ? 'badge-red' : (cancelada || realizada ? 'badge-slate' : 'badge-green');
    const horarioFuturo = new Date(`${c.data}T${c.horario}`) > new Date();
    const possuiProntuario = prontuarios.some(item => String(item.consultaId) === String(c.id));
    const acoes = realizada && possuiProntuario
      ? `<button class="btn btn-prontuario btn-sm" onclick="abrirProntuarioPaciente('${escapar(c.id)}')">Ver prontuário</button>`
      : (c.status === 'AGENDADO' && horarioFuturo
        ? `<button class="btn btn-danger btn-sm" onclick="cancelarConsultaPaciente('${escapar(c.id)}')">Cancelar consulta</button>`
        : (c.status === 'AGENDADO' ? '<small style="color:#64748b">Aguardando atualização do médico</small>' : ''));
    return `
      <div class="consulta-card">
        <div class="date-badge"><div class="day">${escapar(c.data.slice(8, 10))}</div><div class="mon">${escapar(mesAbreviado(c.data))}</div></div>
        <div class="info"><h4>${escapar(c.medicoNome)}</h4><p><strong>Especialidade:</strong> ${escapar(c.nomeEspecialidade || 'Especialidade não informada')}</p><p>${escapar(dataLegivel(c.data))} às ${escapar(c.horario)} · Presencial · Rua Ficticia, nº 100 - São Paulo</p>${cancelada ? '<p><strong>Consulta cancelada. Para reagendar, faça um novo agendamento.</strong></p>' : ''}</div>
        <span class="badge ${badge}">${labelStatus}</span>
        <div class="consulta-acoes">${acoes}</div>
      </div>`;
  }).join('');
}

async function cancelarConsultaPaciente(id) {
  const desejaCancelar = window.confirm('Deseja cancelar esta consulta? Se quiser reagendar, faça um novo agendamento depois do cancelamento.');
  if (!desejaCancelar) return;
  try {
    if (apiPacienteDisponivel) await ConsultaAPI.cancelar(id);
    else ClinicaDemo.cancelarConsulta(id);
    await Promise.all([carregarConsultas(), carregarProntuarios()]);
    renderConsultas();
  } catch (error) {
    window.alert(error.message);
  }
}

function abrirProntuarioPaciente(consultaId) {
  const prontuario = prontuariosPaciente.find(item => String(item.consultaId) === String(consultaId));
  if (!prontuario) {
    window.alert('O prontuário desta consulta ainda não está disponível.');
    return;
  }
  const dataRegistro = prontuario.dataHora ? new Date(prontuario.dataHora) : null;
  document.getElementById('prontuario-data').textContent = dataRegistro && !Number.isNaN(dataRegistro.getTime())
    ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(dataRegistro)
    : 'não informada';
  document.getElementById('prontuario-diagnostico').textContent = prontuario.diagnostico || 'Não informado';
  document.getElementById('prontuario-prescricao').textContent = prontuario.prescricao || 'Não informada';
  document.getElementById('prontuario-observacoes').textContent = prontuario.observacoes || 'Sem observações';
  document.getElementById('modal-prontuario-paciente').classList.remove('hidden');
}

function fecharProntuarioPaciente() {
  document.getElementById('modal-prontuario-paciente').classList.add('hidden');
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
  try {
    [apiMedicos, apiEspecialidades] = await Promise.all([MedicoAPI.listar(), EspecialidadeAPI.listar()]);
    apiPacienteDisponivel = true;
  } catch { /* permite usar o protótipo local enquanto a API não estiver pronta */ }

  const mapa = new Map();
  [...ClinicaDemo.medicosLocais(), ...apiMedicos].forEach(m => {
    if (m.id != null) mapa.set(String(m.id), m);
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

async function atualizarHorarios() {
  const token = ++tokenCarregamentoHorario;
  const medico = medicosDisponiveis.find(item => String(item.id) === String(document.getElementById('select-medico').value));
  const div = document.getElementById('div-horarios');
  const select = document.getElementById('select-horario');
  horariosExibidos = [];
  if (!medico) { div.classList.add('hidden'); select.innerHTML = ''; return; }

  let slots;
  if (apiPacienteDisponivel) {
    try {
      slots = (await AgendaAPI.disponiveis(medico.id)).filter(slot => slot.disponivel);
    } catch {
      slots = ClinicaDemo.horarios().filter(slot => slot.disponivel && String(slot.medicoId) === String(medico.id));
    }
  } else {
    slots = ClinicaDemo.horarios().filter(slot => slot.disponivel && String(slot.medicoId) === String(medico.id));
  }
  if (token !== tokenCarregamentoHorario) return;

  const horarioSelecionado = select.value;
  horariosExibidos = slots.map(slot => ({
    ...slot,
    data: slot.dataAgenda || slot.data,
    horario: String(slot.horario).slice(0, 5),
  })).sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  div.classList.remove('hidden');
  if (!horariosExibidos.length) {
    select.innerHTML = '<option value="">Este médico ainda não disponibilizou horários</option>';
    return;
  }

  select.innerHTML = '<option value="">-- Selecione um horário --</option>' +
    horariosExibidos.map(slot => `<option value="${escapar(slot.id)}">${escapar(dataLegivel(slot.data))} às ${escapar(slot.horario)}</option>`).join('');
  if (horariosExibidos.some(slot => String(slot.id) === String(horarioSelecionado))) {
    select.value = horarioSelecionado;
  }
}

async function confirmarAgendamento() {
  const slotId = document.getElementById('select-horario').value;
  const msg = document.getElementById('msg-agendamento');
  const especialidadeId = document.getElementById('select-especialidade').value;
  const especialidadeNumero = Number(especialidadeId);
  const agendaNumero = Number(slotId);
  const medicoSelecionado = medicosDisponiveis.find(item => String(item.id) === String(document.getElementById('select-medico').value));
  if (!especialidadeId) { msg.textContent = 'Selecione uma especialidade.'; msg.className = 'msg show erro'; return; }
  if (!document.getElementById('select-medico').value) { msg.textContent = 'Selecione um médico.'; msg.className = 'msg show erro'; return; }
  if (!medicoSelecionado || !(medicoSelecionado.especialidadeIds || []).some(id => String(id) === String(especialidadeId))) {
    msg.textContent = 'A especialidade selecionada não corresponde ao médico escolhido.';
    msg.className = 'msg show erro';
    return;
  }
  if (!slotId) { msg.textContent = 'Este médico não tem horários disponíveis. Escolha outro médico ou tente mais tarde.'; msg.className = 'msg show erro'; return; }
  if (!Number.isSafeInteger(especialidadeNumero) || especialidadeNumero <= 0 || (apiPacienteDisponivel && (!Number.isSafeInteger(agendaNumero) || agendaNumero <= 0))) {
    msg.textContent = 'Os identificadores da especialidade ou do horário são inválidos. Atualize a página e tente novamente.';
    msg.className = 'msg show erro';
    return;
  }

  try {
    const observacoes = document.getElementById('obs-consulta').value.trim();
    if (apiPacienteDisponivel) {
      await ConsultaAPI.agendar({ agendaId: agendaNumero, especialidadeId: especialidadeNumero, observacoes });
      await carregarConsultas();
    } else {
      ClinicaDemo.agendarConsulta(slotId, {
        id: sessaoPaciente.id,
        nome: sessaoPaciente.nomePaciente || sessaoPaciente.login,
        especialidadeId: especialidadeNumero,
        nomeEspecialidade: document.getElementById('select-especialidade').selectedOptions[0]?.textContent || 'Especialidade',
        observacoes,
      });
      await carregarConsultas();
    }
    renderConsultas();
    fecharModalAgendamento();
    await atualizarHorarios();
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
    await atualizarHorarios();
  }
}

async function sair() { await encerrarSessao(); }

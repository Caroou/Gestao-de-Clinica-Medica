let sessaoMedico;
let medicoAtual;
let consultaAtual;
let consultasMedico = [];
let atendimentosMedico = [];
let horariosMedico = [];
let apiMedicoDisponivel = false;
let abaConsultasAtiva = 'proximas';
let atualizacaoMedicoEmAndamento = false;

window.addEventListener('DOMContentLoaded', async () => {
  sessaoMedico = await exigirPerfilNoServidor('MEDICO');
  if (!sessaoMedico) return;
  document.getElementById('nome-medico').textContent = sessaoMedico.nomeMedico || sessaoMedico.login;
  const campoData = document.getElementById('agenda-data');
  campoData.min = dataHojeLocal();
  await carregarMedico();
  await Promise.all([carregarConsultas(), carregarHorarios(), carregarAtendimentosMedico()]);
  renderAgenda();
  renderHorarios();
  iniciarAtualizacaoAutomaticaMedico();
});

window.addEventListener('storage', event => {
  if (!apiMedicoDisponivel && ['clinica_demo_consultas', 'clinica_demo_horarios'].includes(event.key)) {
    Promise.all([carregarConsultas(), carregarHorarios(), carregarAtendimentosMedico()]).then(() => { renderAgenda(); renderHorarios(); });
  }
});

function iniciarAtualizacaoAutomaticaMedico() {
  window.setInterval(atualizarDadosMedicoSemRecarregar, 15000);
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') atualizarDadosMedicoSemRecarregar();
  });
  window.addEventListener('focus', atualizarDadosMedicoSemRecarregar);
}

async function atualizarDadosMedicoSemRecarregar() {
  if (!apiMedicoDisponivel || atualizacaoMedicoEmAndamento || document.visibilityState === 'hidden') return;
  atualizacaoMedicoEmAndamento = true;
  try {
    const [consultasApi, horariosApi, atendimentosApi] = await Promise.all([
      ConsultaAPI.doMedico(),
      AgendaAPI.minhas(),
      AtendimentoAPI.doMedico(),
    ]);
    const consultasAtualizadas = consultasApi.map(converterConsulta);
    const consultasMudaram = JSON.stringify(consultasAtualizadas) !== JSON.stringify(consultasMedico);
    const horariosMudaram = JSON.stringify(horariosApi) !== JSON.stringify(horariosMedico);
    const atendimentosMudaram = JSON.stringify(atendimentosApi) !== JSON.stringify(atendimentosMedico);
    if (consultasMudaram) consultasMedico = consultasAtualizadas;
    if (horariosMudaram) horariosMedico = horariosApi;
    if (atendimentosMudaram) atendimentosMedico = atendimentosApi;
    if (consultasMudaram || atendimentosMudaram) renderAgenda();
    if (horariosMudaram) renderHorarios();
  } catch { /* conserva a tela se houver uma falha de rede temporária */ }
  finally { atualizacaoMedicoEmAndamento = false; }
}

function dataHojeLocal() {
  const data = new Date();
  data.setMinutes(data.getMinutes() - data.getTimezoneOffset());
  return data.toISOString().slice(0, 10);
}

function escapar(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

function converterConsulta(item) {
  if (!item.dataHora) return item;
  return {
    ...item,
    data: item.dataHora.slice(0, 10),
    horario: item.dataHora.slice(11, 16),
    pacienteNome: item.nomePaciente,
    medicoNome: item.nomeMedico,
  };
}

async function carregarMedico() {
  try {
    const medicos = await MedicoAPI.listar();
    medicoAtual = medicos.find(item => String(item.usuarioId) === String(sessaoMedico.id));
    apiMedicoDisponivel = Boolean(medicoAtual);
  } catch { /* mantém o fluxo demonstrativo enquanto a API não estiver pronta */ }
  if (!medicoAtual) {
    const local = ClinicaDemo.medicosLocais().find(item => String(item.usuarioId) === String(sessaoMedico.id));
    if (local) medicoAtual = local;
  }
  if (!medicoAtual && sessaoMedico.medicoId != null) {
    medicoAtual = { id: sessaoMedico.medicoId, usuarioId: sessaoMedico.id, nome: sessaoMedico.nomeMedico || sessaoMedico.login };
  }
}

async function carregarConsultas() {
  if (apiMedicoDisponivel) {
    try {
      consultasMedico = (await ConsultaAPI.doMedico()).map(converterConsulta);
      return;
    } catch { /* usa dados da demonstração se o endpoint ainda não responder */ }
  }
  consultasMedico = medicoAtual ? ClinicaDemo.consultas()
    .filter(item => String(item.medicoId) === String(medicoAtual.id) || String(item.medicoUsuarioId) === String(sessaoMedico.id))
    .map(converterConsulta) : [];
}

async function carregarHorarios() {
  if (!medicoAtual) { horariosMedico = []; return; }
  if (apiMedicoDisponivel) {
    try {
      horariosMedico = await AgendaAPI.minhas();
      return;
    } catch { /* usa dados da demonstração se o endpoint ainda não responder */ }
  }
  horariosMedico = ClinicaDemo.horarios().filter(item => String(item.medicoId) === String(medicoAtual.id));
}

async function carregarAtendimentosMedico() {
  if (apiMedicoDisponivel) {
    try {
      atendimentosMedico = await AtendimentoAPI.doMedico();
      return;
    } catch { /* usa os atendimentos locais apenas no modo demonstração */ }
  }
  atendimentosMedico = medicoAtual ? ClinicaDemo.consultas()
    .filter(item => String(item.medicoId) === String(medicoAtual.id) || String(item.medicoUsuarioId) === String(sessaoMedico.id))
    .filter(item => item.status === 'REALIZADO' && item.atendimento)
    .map(item => ({
      consultaId: item.id,
      nomePaciente: item.pacienteNome,
      dataHora: item.atendimento.dataHora || item.atendimento.dataRegistro,
      diagnostico: item.atendimento.diagnostico,
      prescricao: item.atendimento.prescricao,
      observacoes: item.atendimento.observacoes || item.atendimento.observacoesClinicas,
    })) : [];
}

function renderAgenda() {
  const tbody = document.getElementById('tbody-agenda');
  const agendadas = consultasMedico.filter(item => item.status === 'AGENDADO');
  const historico = consultasMedico.filter(item => item.status !== 'AGENDADO');
  document.getElementById('contagem-proximas').textContent = agendadas.length;
  document.getElementById('contagem-historico').textContent = historico.length;
  document.getElementById('total-pacientes').textContent = `${agendadas.length} pendente(s)`;
  document.getElementById('filtro-historico').classList.toggle('hidden', abaConsultasAtiva !== 'historico');
  const busca = document.getElementById('buscar-historico').value.trim().toLocaleLowerCase('pt-BR');
  const consultas = (abaConsultasAtiva === 'proximas' ? agendadas : historico)
    .filter(item => abaConsultasAtiva !== 'historico' || !busca || (item.pacienteNome || '').toLocaleLowerCase('pt-BR').includes(busca))
    .sort((a, b) => abaConsultasAtiva === 'historico'
      ? `${b.data} ${b.horario}`.localeCompare(`${a.data} ${a.horario}`)
      : `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  if (!consultas.length) {
    const vazio = abaConsultasAtiva === 'proximas'
      ? 'Nenhuma consulta pendente. Seus novos agendamentos aparecerão aqui.'
      : (busca ? 'Nenhum atendimento corresponde a esse nome.' : 'O histórico de consultas aparecerá aqui após os atendimentos.');
    tbody.innerHTML = `<tr><td colspan="4" class="agenda-vazia">${vazio}</td></tr>`;
    return;
  }
  tbody.innerHTML = consultas.map(item => {
    const realizada = item.status === 'REALIZADO';
    const cancelada = item.status === 'CANCELADO' || item.status === 'CANCELADA';
    const naoCompareceu = item.status === 'NAO_COMPARECEU';
    const label = realizada ? 'Atendida' : (cancelada ? 'Cancelada' : (naoCompareceu ? 'Não compareceu' : 'Agendada'));
    const badge = realizada ? 'badge-green' : (naoCompareceu ? 'badge-red' : (cancelada ? 'badge-slate' : 'badge-amber'));
    const horarioPassou = new Date(`${item.data}T${item.horario}`) <= new Date();
    const acoes = item.status === 'AGENDADO'
      ? `<div class="consulta-acoes">
          <button class="btn btn-primary btn-sm" onclick="abrirProntuario('${escapar(item.id)}')">Registrar atendimento</button>
          <button class="btn btn-secondary btn-sm" onclick="cancelarConsultaMedico('${escapar(item.id)}')">Cancelar</button>
          <button class="btn btn-secondary btn-sm" onclick="marcarNaoCompareceu('${escapar(item.id)}')" ${horarioPassou ? '' : 'disabled title="Disponível após o horário da consulta"'}>Não compareceu</button>
        </div>`
      : (realizada
        ? `<button class="btn btn-prontuario btn-sm" onclick="abrirProntuarioLeitura('${escapar(item.id)}')">Ver prontuário</button>`
        : '—');
    return `
      <tr>
        <td data-label="Data e horário"><strong>${escapar(item.horario)}</strong><br><small>${escapar(new Intl.DateTimeFormat('pt-BR').format(new Date(`${item.data}T12:00:00`)))}</small></td>
        <td data-label="Paciente"><strong>${escapar(item.pacienteNome)}</strong><br><small class="especialidade-consulta">${escapar(item.nomeEspecialidade || 'Especialidade não informada')}</small><br><small style="color:#64748b">${item.observacoes ? escapar(item.observacoes) : 'Consulta'}</small></td>
        <td data-label="Status"><span class="badge ${badge}">${label}</span></td>
        <td data-label="Ações">${acoes}</td>
      </tr>`;
  }).join('');
}

function mostrarConsultas(aba) {
  abaConsultasAtiva = aba;
  document.getElementById('aba-proximas').classList.toggle('ativa', aba === 'proximas');
  document.getElementById('aba-historico').classList.toggle('ativa', aba === 'historico');
  document.getElementById('aba-proximas').setAttribute('aria-selected', String(aba === 'proximas'));
  document.getElementById('aba-historico').setAttribute('aria-selected', String(aba === 'historico'));
  renderAgenda();
}

async function cancelarConsultaMedico(id) {
  if (!window.confirm('Deseja cancelar esta consulta? O horário futuro voltará a ficar disponível para agendamento.')) return;
  await atualizarStatusConsulta(id, 'CANCELADO');
}

async function marcarNaoCompareceu(id) {
  const consulta = consultasMedico.find(item => String(item.id) === String(id));
  if (!consulta) return;
  if (new Date(`${consulta.data}T${consulta.horario}`) > new Date()) {
    window.alert('A ausência só pode ser registrada depois do horário da consulta.');
    return;
  }
  if (!window.confirm('Confirma que o paciente não compareceu?')) return;
  await atualizarStatusConsulta(id, 'NAO_COMPARECEU');
}

async function atualizarStatusConsulta(id, status) {
  try {
    if (apiMedicoDisponivel) {
      if (status === 'CANCELADO') await ConsultaAPI.cancelarPeloMedico(id);
      else await ConsultaAPI.marcarNaoCompareceu(id);
    } else if (status === 'CANCELADO') {
      const consulta = consultasMedico.find(item => String(item.id) === String(id));
      const futuro = consulta && new Date(`${consulta.data}T${consulta.horario}`) > new Date();
      ClinicaDemo.cancelarConsulta(id, futuro);
    } else {
      ClinicaDemo.marcarNaoCompareceu(id);
    }
    await Promise.all([carregarConsultas(), carregarHorarios()]);
    renderAgenda();
    renderHorarios();
  } catch (error) {
    window.alert(error.message);
  }
}

function renderHorarios() {
  const el = document.getElementById('lista-horarios');
  if (!el) return;
  if (!medicoAtual) {
    el.innerHTML = '<p style="color:#64748b;padding:1rem">Não foi possível vincular este usuário a um cadastro de médico.</p>';
    return;
  }
  const slots = [...horariosMedico].map(item => ({
    ...item,
    data: item.dataAgenda || item.data,
    horario: String(item.horario).slice(0, 5),
  })).sort((a, b) => `${a.data} ${a.horario}`.localeCompare(`${b.data} ${b.horario}`));
  el.innerHTML = slots.length ? slots.map(slot => {
    const vinculadoAoHistorico = consultasMedico.some(consulta =>
      String(consulta.agendaId ?? consulta.slotId) === String(slot.id));
    const acao = slot.disponivel
      ? (vinculadoAoHistorico
        ? '<small style="color:#64748b">Mantido no histórico</small>'
        : `<button class="btn btn-danger btn-sm" onclick="removerHorario('${escapar(slot.id)}')">Remover horário</button>`)
      : '';
    return `
      <div class="consulta-card">
        <div class="info"><h4>${escapar(new Intl.DateTimeFormat('pt-BR').format(new Date(`${slot.data}T12:00:00`)))} às ${escapar(slot.horario)}</h4>
        <p>${slot.disponivel ? 'Disponível para agendamento' : 'Reservado por paciente'}</p></div>
        <span class="badge ${slot.disponivel ? 'badge-green' : 'badge-slate'}">${slot.disponivel ? 'Disponível' : 'Reservado'}</span>
        ${acao}
      </div>`;
  }).join('') : '<p style="color:#64748b;padding:1rem">Nenhum horário disponibilizado ainda.</p>';
}

async function removerHorario(id) {
  if (!window.confirm('Remover este horário disponibilizado?')) return;
  try {
    if (apiMedicoDisponivel) await AgendaAPI.excluir(id);
    else ClinicaDemo.removerHorario(id, medicoAtual.id);
    await Promise.all([carregarHorarios(), carregarConsultas()]);
    renderHorarios();
    renderAgenda();
  } catch (error) {
    window.alert(error.message);
  }
}

function abrirProntuario(id) {
  consultaAtual = consultasMedico.find(item => String(item.id) === String(id));
  if (!consultaAtual) return;
  document.getElementById('titulo-prontuario').textContent = `Atendimento – ${consultaAtual.pacienteNome}`;
  document.getElementById('diagnostico').value = '';
  document.getElementById('prescricao').value = '';
  document.getElementById('obs-clinica').value = '';
  document.getElementById('msg-prontuario').className = 'msg';
  document.getElementById('modal-prontuario').classList.remove('hidden');
}

function abrirProntuarioLeitura(consultaId) {
  const atendimento = atendimentosMedico.find(item => String(item.consultaId) === String(consultaId));
  if (!atendimento) {
    window.alert('O prontuário desta consulta não foi encontrado. Atualize a página e tente novamente.');
    return;
  }
  document.getElementById('titulo-prontuario-leitura').textContent = `Prontuário – ${atendimento.nomePaciente || 'Paciente'}`;
  const data = atendimento.dataHora ? new Date(atendimento.dataHora) : null;
  document.getElementById('data-prontuario-leitura').textContent = data && !Number.isNaN(data.getTime())
    ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(data)
    : 'não informada';
  document.getElementById('diagnostico-prontuario-leitura').textContent = atendimento.diagnostico || 'Não informado';
  document.getElementById('prescricao-prontuario-leitura').textContent = atendimento.prescricao || 'Não informada';
  document.getElementById('observacoes-prontuario-leitura').textContent = atendimento.observacoes || 'Sem observações';
  document.getElementById('modal-prontuario-leitura').classList.remove('hidden');
}

function fecharProntuarioLeitura() {
  document.getElementById('modal-prontuario-leitura').classList.add('hidden');
}

function fecharProntuario() { document.getElementById('modal-prontuario').classList.add('hidden'); }

async function salvarProntuario() {
  const msg = document.getElementById('msg-prontuario');
  const atendimento = {
    diagnostico: document.getElementById('diagnostico').value.trim(),
    prescricao: document.getElementById('prescricao').value.trim(),
    observacoes: document.getElementById('obs-clinica').value.trim(),
  };
  if (!atendimento.diagnostico) { msg.textContent = 'Preencha o diagnóstico.'; msg.className = 'msg show erro'; return; }
  const consultaNumero = Number(consultaAtual?.id);
  if (!Number.isSafeInteger(consultaNumero) || consultaNumero <= 0) {
    msg.textContent = 'Não foi possível identificar a consulta. Atualize a agenda e tente novamente.';
    msg.className = 'msg show erro';
    return;
  }
  if ([atendimento.diagnostico, atendimento.prescricao, atendimento.observacoes]
      .some(texto => new TextEncoder().encode(texto).length > 48000)) {
    msg.textContent = 'Cada campo pode ter até 48 KB de texto.';
    msg.className = 'msg show erro';
    return;
  }
  try {
    if (apiMedicoDisponivel) {
      await AtendimentoAPI.registrar({ ...atendimento, consultaId: consultaNumero });
      await Promise.all([carregarConsultas(), carregarAtendimentosMedico()]);
      renderAgenda();
      fecharProntuario();
      return;
    }
    ClinicaDemo.salvarAtendimento(consultaAtual.id, {
      ...atendimento,
      observacoesClinicas: atendimento.observacoes,
    });
    await Promise.all([carregarConsultas(), carregarAtendimentosMedico()]);
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

async function salvarHorario() {
  const data = document.getElementById('agenda-data').value;
  const hora = document.getElementById('agenda-hora').value;
  const msg = document.getElementById('msg-horario');
  if (!medicoAtual) { msg.textContent = 'O cadastro do médico não foi encontrado. Saia e entre novamente.'; msg.className = 'msg show erro'; return; }
  if (!data || !hora) { msg.textContent = 'Preencha data e horário.'; msg.className = 'msg show erro'; return; }
  if (new Date(`${data}T${hora}`) <= new Date()) { msg.textContent = 'Escolha um horário futuro.'; msg.className = 'msg show erro'; return; }
  try {
    if (apiMedicoDisponivel) {
      await AgendaAPI.cadastrar({ dataAgenda: data, horario: hora });
      await carregarHorarios();
    } else {
      ClinicaDemo.disponibilizarHorario({
        medicoId: medicoAtual.id,
        medicoUsuarioId: sessaoMedico.id,
        medicoNome: medicoAtual.nome || sessaoMedico.nomeMedico || sessaoMedico.login,
        data,
        horario: hora,
      });
      await carregarHorarios();
    }
    renderHorarios();
    msg.textContent = apiMedicoDisponivel ? 'Horário disponibilizado na agenda.' : 'Horário disponibilizado para os pacientes neste navegador.';
    msg.className = 'msg show sucesso';
    setTimeout(fecharModalHorario, 700);
  } catch (error) {
    msg.textContent = error.message;
    msg.className = 'msg show erro';
  }
}

async function sair() { await encerrarSessao(); }

/* Armazenamento temporário do fluxo de agenda, consulta e atendimento.
   Pode ser substituído pelas APIs quando os endpoints estiverem disponíveis. */
const ClinicaDemo = (() => {
  const SLOTS_KEY = 'clinica_demo_horarios';
  const APPTS_KEY = 'clinica_demo_consultas';
  const DOCTORS_KEY = 'clinica_demo_medicos';

  function read(key) {
    try { return JSON.parse(localStorage.getItem(key) || '[]'); }
    catch { return []; }
  }
  function write(key, value) { localStorage.setItem(key, JSON.stringify(value)); }
  function horarios() { return read(SLOTS_KEY); }
  function consultas() { return read(APPTS_KEY); }
  function medicosLocais() { return read(DOCTORS_KEY); }
  function salvarMedico(medico) {
    const lista = medicosLocais().filter(item => String(item.usuarioId) !== String(medico.usuarioId));
    lista.push(medico);
    write(DOCTORS_KEY, lista);
  }
  function disponibilizarHorario(slot) {
    const lista = horarios();
    if (lista.some(item => String(item.medicoId) === String(slot.medicoId) && item.data === slot.data && item.horario === slot.horario)) {
      throw new Error('Este horário já foi cadastrado.');
    }
    lista.push({ ...slot, id: crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`, disponivel: true });
    write(SLOTS_KEY, lista);
  }
  function removerHorario(slotId, medicoId) {
    const lista = horarios();
    const slot = lista.find(item => String(item.id) === String(slotId));
    if (!slot || String(slot.medicoId) !== String(medicoId)) throw new Error('Horário não encontrado na sua agenda.');
    if (!slot.disponivel) throw new Error('Um horário reservado não pode ser removido.');
    write(SLOTS_KEY, lista.filter(item => String(item.id) !== String(slotId)));
  }
  function agendarConsulta(slotId, paciente) {
    const listaHorarios = horarios();
    const slot = listaHorarios.find(item => String(item.id) === String(slotId));
    if (!slot || !slot.disponivel) throw new Error('Este horário não está mais disponível. Atualize a página e escolha outro.');
    if (!paciente.especialidadeId || !(medicosLocais().find(item => String(item.id) === String(slot.medicoId))?.especialidadeIds || []).some(id => String(id) === String(paciente.especialidadeId))) {
      throw new Error('A especialidade selecionada não corresponde ao médico escolhido.');
    }
    slot.disponivel = false;
    write(SLOTS_KEY, listaHorarios);
    const consulta = {
      id: crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`,
      slotId: slot.id,
      medicoId: slot.medicoId,
      medicoUsuarioId: slot.medicoUsuarioId,
      medicoNome: slot.medicoNome,
      pacienteId: paciente.id,
      pacienteNome: paciente.nome,
      especialidadeId: paciente.especialidadeId,
      nomeEspecialidade: paciente.nomeEspecialidade || 'Especialidade não informada',
      data: slot.data,
      horario: slot.horario,
      observacoes: paciente.observacoes || '',
      status: 'AGENDADO',
      atendimento: null,
    };
    write(APPTS_KEY, [...consultas(), consulta]);
    return consulta;
  }
  function salvarAtendimento(consultaId, atendimento) {
    const lista = consultas();
    const consulta = lista.find(item => String(item.id) === String(consultaId));
    if (!consulta) throw new Error('Consulta não encontrada. Atualize a agenda e tente novamente.');
    if (consulta.status !== 'AGENDADO') throw new Error('Somente consultas agendadas podem receber atendimento.');
    if (consulta.atendimento) throw new Error('Já existe um atendimento registrado para esta consulta.');
    consulta.status = 'REALIZADO';
    consulta.atendimento = { ...atendimento, dataRegistro: new Date().toISOString() };
    write(APPTS_KEY, lista);
  }
  function cancelarConsulta(consultaId, liberarHorario = true) {
    const lista = consultas();
    const consulta = lista.find(item => String(item.id) === String(consultaId));
    if (!consulta || consulta.status !== 'AGENDADO') throw new Error('Somente consultas agendadas podem ser canceladas.');
    consulta.status = 'CANCELADO';
    if (liberarHorario) {
      const listaHorarios = horarios();
      const slot = listaHorarios.find(item => String(item.id) === String(consulta.slotId));
      const dataHora = new Date(`${consulta.data}T${consulta.horario}`);
      if (slot && dataHora > new Date()) slot.disponivel = true;
      write(SLOTS_KEY, listaHorarios);
    }
    write(APPTS_KEY, lista);
  }
  function marcarNaoCompareceu(consultaId) {
    const lista = consultas();
    const consulta = lista.find(item => String(item.id) === String(consultaId));
    if (!consulta || consulta.status !== 'AGENDADO') throw new Error('Somente consultas agendadas podem ser atualizadas.');
    if (new Date(`${consulta.data}T${consulta.horario}`) > new Date()) throw new Error('A ausência só pode ser registrada depois do horário da consulta.');
    consulta.status = 'NAO_COMPARECEU';
    write(APPTS_KEY, lista);
  }
  return { horarios, consultas, medicosLocais, salvarMedico, disponibilizarHorario, removerHorario, agendarConsulta, salvarAtendimento, cancelarConsulta, marcarNaoCompareceu };
})();

function somenteDigitos(valor) {
  return String(valor || '').replace(/\D/g, '');
}

function aplicarMascaraCpf(valor) {
  const digitos = somenteDigitos(valor).slice(0, 11);
  return digitos
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
}

function aplicarMascaraTelefone(valor) {
  const digitos = somenteDigitos(valor).slice(0, 11);
  if (digitos.length <= 2) return digitos ? `(${digitos}` : '';
  if (digitos.length <= 6) return `(${digitos.slice(0, 2)}) ${digitos.slice(2)}`;
  const tamanhoLocal = digitos.length > 10 ? 5 : 4;
  return `(${digitos.slice(0, 2)}) ${digitos.slice(2, 2 + tamanhoLocal)}-${digitos.slice(2 + tamanhoLocal)}`;
}

function telefoneValido(valor) {
  return /^\d{10,11}$/.test(somenteDigitos(valor));
}

function ativarMascarasContato() {
  document.querySelectorAll('[data-mascara="cpf"]').forEach(campo => {
    campo.addEventListener('input', () => { campo.value = aplicarMascaraCpf(campo.value); });
  });
  document.querySelectorAll('[data-mascara="telefone"]').forEach(campo => {
    campo.addEventListener('input', () => { campo.value = aplicarMascaraTelefone(campo.value); });
  });
}

window.somenteDigitos = somenteDigitos;
window.aplicarMascaraCpf = aplicarMascaraCpf;
window.aplicarMascaraTelefone = aplicarMascaraTelefone;
window.telefoneValido = telefoneValido;

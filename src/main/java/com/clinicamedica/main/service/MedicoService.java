package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.MedicoCadastroDTO;
import com.clinicamedica.main.dto.MedicoAdminAtualizacaoDTO;
import com.clinicamedica.main.dto.MedicoRetornoDTO;
import com.clinicamedica.main.model.Especialidade;
import com.clinicamedica.main.model.Medico;
import com.clinicamedica.main.model.PerfilUsuario;
import com.clinicamedica.main.model.Usuario;
import com.clinicamedica.main.repository.EspecialidadeRepository;
import com.clinicamedica.main.repository.MedicoRepository;
import com.clinicamedica.main.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.clinicamedica.main.validation.ValidacaoDados;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public MedicoService(
            MedicoRepository medicoRepository,
            UsuarioRepository usuarioRepository,
            EspecialidadeRepository especialidadeRepository) {

        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    @org.springframework.transaction.annotation.Transactional
    public MedicoRetornoDTO cadastrarMedico(MedicoCadastroDTO dados, Integer usuarioAutenticadoId) {
        if (dados == null) throw new IllegalArgumentException("Dados do médico são obrigatórios.");
        if (dados.usuarioId() == null || !dados.usuarioId().equals(usuarioAutenticadoId)) {
            throw new IllegalArgumentException("O médico só pode concluir o cadastro da própria conta.");
        }


        // 1. Validação do CRM
        String nome = ValidacaoDados.textoObrigatorio(dados.nome(), "o nome", 255);
        String telefone = ValidacaoDados.telefone(dados.telefone());
        String crm = ValidacaoDados.textoObrigatorio(dados.crm(), "o CRM", 50);

        if (medicoRepository.existsByCrm(crm)) {
            throw new IllegalArgumentException(
                    "Este CRM já está cadastrado."
            );
        }

        // 2. Busca o usuário vinculado
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário vinculado não encontrado."
                        ));
        if (!usuario.isAtivo() || usuario.getPerfilUsuario() != PerfilUsuario.MEDICO) {
            throw new IllegalArgumentException("O usuário vinculado não possui perfil médico ativo.");
        }

        // 3. Cria o médico
        Medico medico = new Medico();

        medico.setUsuario(usuario);
        medico.setNome(nome);
        medico.setCrm(crm);
        medico.setTelefone(telefone);

        // 4. Busca e adiciona as especialidades
        if (dados.especialidadeIds() == null || dados.especialidadeIds().isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma especialidade.");
        }
        if (dados.especialidadeIds().stream().anyMatch(id -> id == null || id <= 0)
                || dados.especialidadeIds().stream().distinct().count() != dados.especialidadeIds().size()) {
            throw new IllegalArgumentException("A lista de especialidades contém identificadores inválidos ou repetidos.");
        }
        for (Integer especialidadeId : dados.especialidadeIds()) {
            Especialidade especialidade = especialidadeRepository.findById(especialidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada: " + especialidadeId));
            medico.getEspecialidades().add(especialidade);
        }

        // 5. Salva
        Medico salvo = medicoRepository.save(medico);

        // 6. Retorna DTO
        return new MedicoRetornoDTO(salvo);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<MedicoRetornoDTO> listarTodos() {

        return medicoRepository.findAll()
                .stream()
                .map(MedicoRetornoDTO::new)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public MedicoRetornoDTO atualizarDadosAdministrativos(Integer id, MedicoAdminAtualizacaoDTO dados) {
        if (dados == null) throw new IllegalArgumentException("Dados do médico são obrigatórios.");

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));
        String nome = ValidacaoDados.textoObrigatorio(dados.nome(), "o nome", 255);
        String crm = ValidacaoDados.textoObrigatorio(dados.crm(), "o CRM", 50);
        String telefone = ValidacaoDados.telefone(dados.telefone());
        if (!crm.equalsIgnoreCase(medico.getCrm()) && medicoRepository.existsByCrmAndIdNot(crm, id)) {
            throw new IllegalArgumentException("Este CRM já está sendo usado por outro médico.");
        }

        medico.setNome(nome);
        medico.setCrm(crm);
        medico.setTelefone(telefone);
        return new MedicoRetornoDTO(medicoRepository.save(medico));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public MedicoRetornoDTO buscarPorId(Integer id) {

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Médico não encontrado."
                        ));

        return new MedicoRetornoDTO(medico);
    }

    public void excluir(Integer id) {

        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException(
                    "Médico não encontrado."
            );
        }

        medicoRepository.deleteById(id);
    }
}

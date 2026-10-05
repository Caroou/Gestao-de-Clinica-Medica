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
        if (dados.usuarioId() == null || !dados.usuarioId().equals(usuarioAutenticadoId)) {
            throw new IllegalArgumentException("O médico só pode concluir o cadastro da própria conta.");
        }


        // 1. Validação do CRM
        if (dados.crm() == null || dados.crm().isBlank()) {
            throw new IllegalArgumentException(
                    "O CRM é obrigatório."
            );
        }

        String crm = dados.crm().trim();

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
        medico.setNome(dados.nome());
        medico.setCrm(crm);
        medico.setTelefone(dados.telefone());

        // 4. Busca e adiciona as especialidades
        System.out.println("DEBUG - IDs recebidos no JSON: " + dados.especialidadeIds());

        if (dados.especialidadeIds() != null && !dados.especialidadeIds().isEmpty()) {

            for (Integer especialidadeId : dados.especialidadeIds()) {

                Especialidade especialidade =
                        especialidadeRepository.findById(especialidadeId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Especialidade não encontrada: "
                                                        + especialidadeId
                                        ));

                System.out.println("DEBUG - Vinculando especialidade: " + especialidade.getNome());
                medico.getEspecialidades().add(especialidade);
            }
        } else {
            System.out.println("DEBUG - A lista de especialidadeIds veio NULA ou VAZIA do Postman!");
        }

        // 5. Salva
        Medico salvo = medicoRepository.save(medico);

        // 6. Retorna DTO
        return new MedicoRetornoDTO(salvo);
    }

    public List<MedicoRetornoDTO> listarTodos() {

        return medicoRepository.findAll()
                .stream()
                .map(MedicoRetornoDTO::new)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public MedicoRetornoDTO atualizarDadosAdministrativos(Integer id, MedicoAdminAtualizacaoDTO dados) {
        if (dados.nome() == null || dados.nome().isBlank()
                || dados.crm() == null || dados.crm().isBlank()
                || dados.telefone() == null || dados.telefone().isBlank()) {
            throw new IllegalArgumentException("Informe nome, CRM e telefone.");
        }

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));
        String crm = dados.crm().trim();
        if (!crm.equalsIgnoreCase(medico.getCrm()) && medicoRepository.existsByCrmAndIdNot(crm, id)) {
            throw new IllegalArgumentException("Este CRM já está sendo usado por outro médico.");
        }

        medico.setNome(dados.nome().trim());
        medico.setCrm(crm);
        medico.setTelefone(dados.telefone().trim());
        return new MedicoRetornoDTO(medicoRepository.save(medico));
    }

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

package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.PacienteCadastroDTO;
import com.clinicamedica.main.dto.PacienteRetornoDTO;
import com.clinicamedica.main.model.Paciente;
import com.clinicamedica.main.model.Usuario;
import com.clinicamedica.main.repository.PacienteRepository;
import com.clinicamedica.main.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    // Injetamos os dois Repositories porque o Paciente precisa do Usuario para nascer
    public PacienteService(PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PacienteRetornoDTO cadastrarPaciente(PacienteCadastroDTO dados) {
        // 1. Validações de duplicidade
        if (pacienteRepository.existsByCpf(dados.cpf())) {
            throw new IllegalArgumentException("Este CPF já está cadastrado.");
        }
        if (pacienteRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException("Este E-mail já está cadastrado.");
        }

        // 2. Busca o Usuário no banco para fazer a relação (Chave Estrangeira)
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário vinculado não encontrado."));

        // 3. Monta a entidade Paciente com os dados do DTO
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setNome(dados.nome());
        paciente.setCpf(dados.cpf());
        paciente.setDataNascimento(dados.dataNascimento());
        paciente.setTelefone(dados.telefone());
        paciente.setEmail(dados.email());

        // 4. Salva no banco.
        // É exatamente AQUI que a mágica acontece: o Hibernate vai chamar o seu
        // CriptografiaAesConverter automaticamente e salvar o CPF e o Telefone embaralhados!
        pacienteRepository.save(paciente);

        // 5. Devolve o DTO limpo
        return new PacienteRetornoDTO(paciente);
    }

    // Já vamos deixar pronto um método para listar todos os pacientes na tela
    public List<PacienteRetornoDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(PacienteRetornoDTO::new) // Converte cada Paciente do banco para o DTO de retorno
                .toList();
    }
}
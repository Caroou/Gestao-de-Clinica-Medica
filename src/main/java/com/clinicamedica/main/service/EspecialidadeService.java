package com.clinica.medica.service;

import com.clinica.medica.dto.EspecialidadeRequestDTO;
import com.clinica.medica.dto.EspecialidadeResponseDTO;
import com.clinica.medica.model.Especialidade;
import com.clinica.medica.repository.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository repository;

    public EspecialidadeService(EspecialidadeRepository repository) {
        this.repository = repository;
    }

    public EspecialidadeResponseDTO cadastrar(EspecialidadeRequestDTO dto) {

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("O nome da especialidade é obrigatório.");
        }

        String nome = dto.nome().trim();

        if (repository.existsByNome(nome)) {
            throw new IllegalArgumentException(
                    "Já existe uma especialidade cadastrada com esse nome."
            );
        }

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(nome);

        Especialidade salva = repository.save(especialidade);

        return converterParaResponse(salva);
    }

    public List<EspecialidadeResponseDTO> listarTodos() {

        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public EspecialidadeResponseDTO buscarPorId(Integer id) {

        Especialidade especialidade = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Especialidade não encontrada.")
                );

        return converterParaResponse(especialidade);
    }

    public EspecialidadeResponseDTO atualizar(
            Integer id,
            EspecialidadeRequestDTO dto
    ) {

        Especialidade especialidade = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Especialidade não encontrada.")
                );

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da especialidade é obrigatório."
            );
        }

        String nome = dto.nome().trim();

        if (!nome.equalsIgnoreCase(especialidade.getNome())
                && repository.existsByNome(nome)) {

            throw new IllegalArgumentException(
                    "Já existe uma especialidade cadastrada com esse nome."
            );
        }

        especialidade.setNome(nome);

        Especialidade atualizada = repository.save(especialidade);

        return converterParaResponse(atualizada);
    }

    public void excluir(Integer id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Especialidade não encontrada.");
        }

        repository.deleteById(id);
    }

    private EspecialidadeResponseDTO converterParaResponse(
            Especialidade especialidade
    ) {

        return new EspecialidadeResponseDTO(
                especialidade.getId(),
                especialidade.getNome()
        );
    }
}
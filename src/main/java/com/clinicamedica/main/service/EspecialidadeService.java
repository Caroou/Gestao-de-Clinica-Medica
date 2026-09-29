package com.clinicamedica.main.service;

import com.clinicamedica.main.dto.EspecialidadeCadastroDTO;
import com.clinicamedica.main.dto.EspecialidadeRetornoDTO;
import com.clinicamedica.main.model.Especialidade;
import com.clinicamedica.main.repository.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository repository;

    public EspecialidadeService(EspecialidadeRepository repository) {
        this.repository = repository;
    }

    public EspecialidadeRetornoDTO cadastrar(EspecialidadeCadastroDTO dto) {

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

    public List<EspecialidadeRetornoDTO> listarTodos() {

        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public EspecialidadeRetornoDTO buscarPorId(Integer id) {

        Especialidade especialidade = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Especialidade não encontrada.")
                );

        return converterParaResponse(especialidade);
    }

    public EspecialidadeRetornoDTO atualizar(
            Integer id,
            EspecialidadeRetornoDTO dto
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

    private EspecialidadeRetornoDTO converterParaResponse(
            Especialidade especialidade
    ) {

        return new EspecialidadeRetornoDTO(
                especialidade.getId(),
                especialidade.getNome()
        );
    }
}
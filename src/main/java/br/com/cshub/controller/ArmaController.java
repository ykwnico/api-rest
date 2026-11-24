package br.com.cshub.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.cshub.entity.Arma;
import br.com.cshub.repository.ArmaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Rotas de armas: todas centralizadas em /api/armas
@RestController
@RequestMapping("/api/armas")
@Tag(name = "Armas", description = "Cadastro de armas e preços")
public class ArmaController {

    private final ArmaRepository armaRepository;

    public ArmaController(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @GetMapping
    @Operation(summary = "Lista todas as armas ordenadas por categoria e preço")
    public List<Arma> listar() {
        return armaRepository.findAll(Sort.by("categoria", "preco"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma arma pelo ID")
    public Arma buscarPorId(@PathVariable Long id) {
        return armaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Arma com ID " + id + " não encontrada"));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova arma")
    public ResponseEntity<Arma> cadastrar(@Valid @RequestBody Arma arma) {
        if (armaRepository.existsByNomeIgnoreCase(arma.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma arma com o nome " + arma.getNome());
        }
        arma.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(armaRepository.save(arma));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma arma existente")
    public Arma atualizar(@PathVariable Long id, @Valid @RequestBody Arma arma) {
        if (!armaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arma com ID " + id + " não encontrada");
        }
        if (armaRepository.existsByNomeIgnoreCaseAndIdNot(arma.getNome(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra arma com o nome " + arma.getNome());
        }
        arma.setId(id);
        return armaRepository.save(arma);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma arma")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!armaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Arma com ID " + id + " não encontrada");
        }
        armaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

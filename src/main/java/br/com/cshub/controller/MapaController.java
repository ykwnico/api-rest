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

import br.com.cshub.entity.Mapa;
import br.com.cshub.repository.MapaRepository;
import br.com.cshub.repository.PartidaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Rotas de mapas: todas centralizadas em /api/mapas
@RestController
@RequestMapping("/api/mapas")
@Tag(name = "Mapas", description = "Cadastro de mapas")
public class MapaController {

    private final MapaRepository mapaRepository;
    private final PartidaRepository partidaRepository;

    public MapaController(MapaRepository mapaRepository, PartidaRepository partidaRepository) {
        this.mapaRepository = mapaRepository;
        this.partidaRepository = partidaRepository;
    }

    @GetMapping
    @Operation(summary = "Lista todos os mapas em ordem alfabética")
    public List<Mapa> listar() {
        return mapaRepository.findAll(Sort.by("nome"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um mapa pelo ID")
    public Mapa buscarPorId(@PathVariable Long id) {
        return mapaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapa com ID " + id + " não encontrado"));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo mapa")
    public ResponseEntity<Mapa> cadastrar(@Valid @RequestBody Mapa mapa) {
        if (mapaRepository.existsByNomeIgnoreCase(mapa.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um mapa com o nome " + mapa.getNome());
        }
        mapa.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapaRepository.save(mapa));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um mapa existente")
    public Mapa atualizar(@PathVariable Long id, @Valid @RequestBody Mapa mapa) {
        if (!mapaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapa com ID " + id + " não encontrado");
        }
        if (mapaRepository.existsByNomeIgnoreCaseAndIdNot(mapa.getNome(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro mapa com o nome " + mapa.getNome());
        }
        mapa.setId(id);
        return mapaRepository.save(mapa);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um mapa (somente se não estiver em partidas)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!mapaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapa com ID " + id + " não encontrado");
        }
        if (partidaRepository.existsByMapaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: o mapa aparece em partidas cadastradas.");
        }
        mapaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

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

import br.com.cshub.entity.Jogador;
import br.com.cshub.entity.Time;
import br.com.cshub.repository.JogadorRepository;
import br.com.cshub.repository.TimeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Rotas de jogadores: todas centralizadas em /api/jogadores
@RestController
@RequestMapping("/api/jogadores")
@Tag(name = "Jogadores", description = "Cadastro de jogadores")
public class JogadorController {

    private final JogadorRepository jogadorRepository;
    private final TimeRepository timeRepository;

    public JogadorController(JogadorRepository jogadorRepository, TimeRepository timeRepository) {
        this.jogadorRepository = jogadorRepository;
        this.timeRepository = timeRepository;
    }

    @GetMapping
    @Operation(summary = "Lista todos os jogadores em ordem alfabética de nickname")
    public List<Jogador> listar() {
        return jogadorRepository.findAll(Sort.by("nickname"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um jogador pelo ID")
    public Jogador buscarPorId(@PathVariable Long id) {
        return jogadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " não encontrado"));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo jogador (o time é opcional)")
    public ResponseEntity<Jogador> cadastrar(@Valid @RequestBody Jogador jogador) {
        if (jogadorRepository.existsByNicknameIgnoreCase(jogador.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um jogador com o nickname " + jogador.getNickname());
        }
        jogador.setId(null);
        jogador.setTime(carregarTime(jogador.getTime()));
        return ResponseEntity.status(HttpStatus.CREATED).body(jogadorRepository.save(jogador));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um jogador existente")
    public Jogador atualizar(@PathVariable Long id, @Valid @RequestBody Jogador jogador) {
        if (!jogadorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " não encontrado");
        }
        if (jogadorRepository.existsByNicknameIgnoreCaseAndIdNot(jogador.getNickname(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro jogador com o nickname " + jogador.getNickname());
        }
        jogador.setId(id);
        jogador.setTime(carregarTime(jogador.getTime()));
        return jogadorRepository.save(jogador);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um jogador")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!jogadorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " não encontrado");
        }
        jogadorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Busca o time completo no banco a partir do ID enviado no JSON.
    // Se nenhum time for enviado, o jogador fica sem time (free agent).
    private Time carregarTime(Time time) {
        if (time == null || time.getId() == null) {
            return null;
        }
        return timeRepository.findById(time.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O time informado (ID " + time.getId() + ") não existe"));
    }
}

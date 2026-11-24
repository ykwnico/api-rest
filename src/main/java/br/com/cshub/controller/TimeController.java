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

import br.com.cshub.entity.Time;
import br.com.cshub.repository.JogadorRepository;
import br.com.cshub.repository.PartidaRepository;
import br.com.cshub.repository.TimeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Rotas de times: todas centralizadas em /api/times
@RestController
@RequestMapping("/api/times")
@Tag(name = "Times", description = "Cadastro de equipes")
public class TimeController {

    private final TimeRepository timeRepository;
    private final JogadorRepository jogadorRepository;
    private final PartidaRepository partidaRepository;

    public TimeController(TimeRepository timeRepository, JogadorRepository jogadorRepository,
                          PartidaRepository partidaRepository) {
        this.timeRepository = timeRepository;
        this.jogadorRepository = jogadorRepository;
        this.partidaRepository = partidaRepository;
    }

    @GetMapping
    @Operation(summary = "Lista todos os times ordenados pelo ranking")
    public List<Time> listar() {
        return timeRepository.findAll(Sort.by("ranking"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um time pelo ID")
    public Time buscarPorId(@PathVariable Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time com ID " + id + " não encontrado"));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo time")
    public ResponseEntity<Time> cadastrar(@Valid @RequestBody Time time) {
        if (timeRepository.existsByNomeIgnoreCase(time.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um time com o nome " + time.getNome());
        }
        time.setId(null); // garante que o banco vai gerar o ID
        return ResponseEntity.status(HttpStatus.CREATED).body(timeRepository.save(time));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um time existente")
    public Time atualizar(@PathVariable Long id, @Valid @RequestBody Time time) {
        if (!timeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time com ID " + id + " não encontrado");
        }
        if (timeRepository.existsByNomeIgnoreCaseAndIdNot(time.getNome(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro time com o nome " + time.getNome());
        }
        time.setId(id);
        return timeRepository.save(time);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um time (somente se não tiver jogadores nem partidas)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!timeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time com ID " + id + " não encontrado");
        }
        // Regra de negócio: não apagar time que ainda está em uso
        if (jogadorRepository.existsByTimeId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: o time possui jogadores vinculados. Transfira ou remova os jogadores antes.");
        }
        if (partidaRepository.existsByTimeAIdOrTimeBId(id, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: o time aparece em partidas cadastradas.");
        }
        timeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

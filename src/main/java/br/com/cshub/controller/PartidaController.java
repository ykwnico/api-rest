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
import br.com.cshub.entity.Partida;
import br.com.cshub.entity.Time;
import br.com.cshub.repository.MapaRepository;
import br.com.cshub.repository.PartidaRepository;
import br.com.cshub.repository.TimeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Rotas de partidas: todas centralizadas em /api/partidas
@RestController
@RequestMapping("/api/partidas")
@Tag(name = "Partidas", description = "Registro de partidas e resultados")
public class PartidaController {

    private final PartidaRepository partidaRepository;
    private final TimeRepository timeRepository;
    private final MapaRepository mapaRepository;

    public PartidaController(PartidaRepository partidaRepository, TimeRepository timeRepository,
                             MapaRepository mapaRepository) {
        this.partidaRepository = partidaRepository;
        this.timeRepository = timeRepository;
        this.mapaRepository = mapaRepository;
    }

    @GetMapping
    @Operation(summary = "Lista todas as partidas, das mais recentes para as mais antigas")
    public List<Partida> listar() {
        return partidaRepository.findAll(Sort.by(Sort.Direction.DESC, "data"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma partida pelo ID")
    public Partida buscarPorId(@PathVariable Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida com ID " + id + " não encontrada"));
    }

    @PostMapping
    @Operation(summary = "Registra uma nova partida")
    public ResponseEntity<Partida> cadastrar(@Valid @RequestBody Partida partida) {
        partida.setId(null);
        prepararRelacionamentos(partida);
        return ResponseEntity.status(HttpStatus.CREATED).body(partidaRepository.save(partida));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma partida existente")
    public Partida atualizar(@PathVariable Long id, @Valid @RequestBody Partida partida) {
        if (!partidaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida com ID " + id + " não encontrada");
        }
        partida.setId(id);
        prepararRelacionamentos(partida);
        return partidaRepository.save(partida);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma partida")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!partidaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida com ID " + id + " não encontrada");
        }
        partidaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Carrega os times e o mapa completos do banco e aplica as regras da partida
    private void prepararRelacionamentos(Partida partida) {
        Time timeA = carregarTime(partida.getTimeA(), "A");
        Time timeB = carregarTime(partida.getTimeB(), "B");

        // Regra: um time não pode jogar contra ele mesmo
        if (timeA.getId().equals(timeB.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O time A e o time B devem ser diferentes");
        }

        Long mapaId = partida.getMapa().getId();
        if (mapaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID do mapa");
        }
        Mapa mapa = mapaRepository.findById(mapaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O mapa informado (ID " + mapaId + ") não existe"));

        partida.setTimeA(timeA);
        partida.setTimeB(timeB);
        partida.setMapa(mapa);
    }

    private Time carregarTime(Time time, String lado) {
        if (time.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID do time " + lado);
        }
        return timeRepository.findById(time.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O time " + lado + " informado (ID " + time.getId() + ") não existe"));
    }
}

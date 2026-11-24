package br.com.cshub.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Representa uma partida entre dois times em um mapa
@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campeonato é obrigatório")
    @Size(max = 80, message = "O campeonato deve ter no máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String campeonato;

    @NotNull(message = "A data da partida é obrigatória")
    @Column(name = "data_partida", nullable = false)
    private LocalDate data;

    // Times que se enfrentam. No JSON basta enviar { "id": 1 }
    @NotNull(message = "O time A é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "time_a_id", nullable = false)
    private Time timeA;

    @NotNull(message = "O time B é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "time_b_id", nullable = false)
    private Time timeB;

    @NotNull(message = "O mapa é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "mapa_id", nullable = false)
    private Mapa mapa;

    // Placar em rounds (no CS2 uma partida regular vai até 13 rounds)
    @NotNull(message = "O placar do time A é obrigatório")
    @Min(value = 0, message = "O placar do time A não pode ser negativo")
    @Max(value = 50, message = "O placar do time A deve ser no máximo 50")
    @Column(name = "placar_a", nullable = false)
    private Integer placarA;

    @NotNull(message = "O placar do time B é obrigatório")
    @Min(value = 0, message = "O placar do time B não pode ser negativo")
    @Max(value = 50, message = "O placar do time B deve ser no máximo 50")
    @Column(name = "placar_b", nullable = false)
    private Integer placarB;

    // Construtor vazio exigido pelo JPA
    public Partida() {
    }

    // Construtor com todos os atributos
    public Partida(Long id, String campeonato, LocalDate data, Time timeA, Time timeB,
                   Mapa mapa, Integer placarA, Integer placarB) {
        this.id = id;
        this.campeonato = campeonato;
        this.data = data;
        this.timeA = timeA;
        this.timeB = timeB;
        this.mapa = mapa;
        this.placarA = placarA;
        this.placarB = placarB;
    }

    // Construtor sem o ID (usado para novos cadastros)
    public Partida(String campeonato, LocalDate data, Time timeA, Time timeB,
                   Mapa mapa, Integer placarA, Integer placarB) {
        this.campeonato = campeonato;
        this.data = data;
        this.timeA = timeA;
        this.timeB = timeB;
        this.mapa = mapa;
        this.placarA = placarA;
        this.placarB = placarB;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Time getTimeA() {
        return timeA;
    }

    public void setTimeA(Time timeA) {
        this.timeA = timeA;
    }

    public Time getTimeB() {
        return timeB;
    }

    public void setTimeB(Time timeB) {
        this.timeB = timeB;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
    }

    public Integer getPlacarA() {
        return placarA;
    }

    public void setPlacarA(Integer placarA) {
        this.placarA = placarA;
    }

    public Integer getPlacarB() {
        return placarB;
    }

    public void setPlacarB(Integer placarB) {
        this.placarB = placarB;
    }
}

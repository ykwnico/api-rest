package br.com.cshub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Representa um jogador (player). Pode estar sem time (free agent).
@Entity
@Table(name = "jogadores")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nickname é obrigatório")
    @Size(max = 30, message = "O nickname deve ter no máximo 30 caracteres")
    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @NotBlank(message = "O nome real é obrigatório")
    @Size(max = 80, message = "O nome real deve ter no máximo 80 caracteres")
    @Column(name = "nome_real", nullable = false, length = 80)
    private String nomeReal;

    @NotBlank(message = "A nacionalidade é obrigatória")
    @Size(max = 40, message = "A nacionalidade deve ter no máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String nacionalidade;

    // Função dentro do time
    @NotBlank(message = "A função é obrigatória")
    @Pattern(regexp = "AWPer|Entry|IGL|Suporte|Lurker|Rifler",
            message = "A função deve ser AWPer, Entry, IGL, Suporte, Lurker ou Rifler")
    @Column(nullable = false, length = 20)
    private String funcao;

    // Rating 2.0 (média de desempenho)
    @NotNull(message = "O rating é obrigatório")
    @DecimalMin(value = "0.0", message = "O rating não pode ser negativo")
    @DecimalMax(value = "3.0", message = "O rating deve ser no máximo 3.0")
    @Column(nullable = false)
    private Double rating;

    // Time atual do jogador (opcional). No JSON basta enviar { "id": 1 }
    @ManyToOne
    @JoinColumn(name = "time_id")
    private Time time;

    // Construtor vazio exigido pelo JPA
    public Jogador() {
    }

    // Construtor com todos os atributos
    public Jogador(Long id, String nickname, String nomeReal, String nacionalidade,
                   String funcao, Double rating, Time time) {
        this.id = id;
        this.nickname = nickname;
        this.nomeReal = nomeReal;
        this.nacionalidade = nacionalidade;
        this.funcao = funcao;
        this.rating = rating;
        this.time = time;
    }

    // Construtor sem o ID (usado para novos cadastros)
    public Jogador(String nickname, String nomeReal, String nacionalidade,
                   String funcao, Double rating, Time time) {
        this.nickname = nickname;
        this.nomeReal = nomeReal;
        this.nacionalidade = nacionalidade;
        this.funcao = funcao;
        this.rating = rating;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNomeReal() {
        return nomeReal;
    }

    public void setNomeReal(String nomeReal) {
        this.nomeReal = nomeReal;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}

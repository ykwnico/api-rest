package br.com.cshub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Representa um mapa do jogo (Mirage, Inferno, Nuke...)
@Entity
@Table(name = "mapas")
public class Mapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do mapa é obrigatório")
    @Size(max = 40, message = "O nome do mapa deve ter no máximo 40 caracteres")
    @Column(nullable = false, unique = true, length = 40)
    private String nome;

    // Modo de jogo do mapa
    @NotBlank(message = "O modo é obrigatório")
    @Pattern(regexp = "Competitivo|Wingman|Casual",
            message = "O modo deve ser Competitivo, Wingman ou Casual")
    @Column(nullable = false, length = 20)
    private String modo;

    // Indica se o mapa está no Active Duty (pool oficial dos campeonatos)
    @NotNull(message = "Informe se o mapa está no pool ativo")
    @Column(name = "pool_ativo", nullable = false)
    private Boolean poolAtivo;

    @NotNull(message = "O ano de lançamento é obrigatório")
    @Min(value = 1999, message = "O ano de lançamento deve ser a partir de 1999")
    @Max(value = 2100, message = "O ano de lançamento é inválido")
    @Column(name = "ano_lancamento", nullable = false)
    private Integer anoLancamento;

    // Construtor vazio exigido pelo JPA
    public Mapa() {
    }

    // Construtor com todos os atributos
    public Mapa(Long id, String nome, String modo, Boolean poolAtivo, Integer anoLancamento) {
        this.id = id;
        this.nome = nome;
        this.modo = modo;
        this.poolAtivo = poolAtivo;
        this.anoLancamento = anoLancamento;
    }

    // Construtor sem o ID (usado para novos cadastros)
    public Mapa(String nome, String modo, Boolean poolAtivo, Integer anoLancamento) {
        this.nome = nome;
        this.modo = modo;
        this.poolAtivo = poolAtivo;
        this.anoLancamento = anoLancamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public Boolean getPoolAtivo() {
        return poolAtivo;
    }

    public void setPoolAtivo(Boolean poolAtivo) {
        this.poolAtivo = poolAtivo;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }
}

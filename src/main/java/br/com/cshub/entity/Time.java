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
import jakarta.validation.constraints.Size;

// Representa uma equipe (time) profissional ou amadora de Counter-Strike
@Entity
@Table(name = "times")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do time é obrigatório")
    @Size(max = 60, message = "O nome do time deve ter no máximo 60 caracteres")
    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @NotBlank(message = "O país é obrigatório")
    @Size(max = 40, message = "O país deve ter no máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String pais;

    // Posição no ranking mundial (1 = melhor)
    @NotNull(message = "O ranking é obrigatório")
    @Min(value = 1, message = "O ranking deve ser maior ou igual a 1")
    @Max(value = 500, message = "O ranking deve ser menor ou igual a 500")
    @Column(nullable = false)
    private Integer ranking;

    @NotNull(message = "O ano de fundação é obrigatório")
    @Min(value = 1999, message = "O ano de fundação deve ser a partir de 1999")
    @Max(value = 2100, message = "O ano de fundação é inválido")
    @Column(name = "ano_fundacao", nullable = false)
    private Integer anoFundacao;

    // Construtor vazio exigido pelo JPA
    public Time() {
    }

    // Construtor com todos os atributos
    public Time(Long id, String nome, String pais, Integer ranking, Integer anoFundacao) {
        this.id = id;
        this.nome = nome;
        this.pais = pais;
        this.ranking = ranking;
        this.anoFundacao = anoFundacao;
    }

    // Construtor sem o ID (usado para novos cadastros)
    public Time(String nome, String pais, Integer ranking, Integer anoFundacao) {
        this.nome = nome;
        this.pais = pais;
        this.ranking = ranking;
        this.anoFundacao = anoFundacao;
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getAnoFundacao() {
        return anoFundacao;
    }

    public void setAnoFundacao(Integer anoFundacao) {
        this.anoFundacao = anoFundacao;
    }
}

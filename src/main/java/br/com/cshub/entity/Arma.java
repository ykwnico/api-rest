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

// Representa uma arma do jogo, com preço na loja e lado que pode comprar
@Entity
@Table(name = "armas")
public class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da arma é obrigatório")
    @Size(max = 40, message = "O nome da arma deve ter no máximo 40 caracteres")
    @Column(nullable = false, unique = true, length = 40)
    private String nome;

    @NotBlank(message = "A categoria é obrigatória")
    @Pattern(regexp = "Pistola|SMG|Rifle|Sniper|Pesada",
            message = "A categoria deve ser Pistola, SMG, Rifle, Sniper ou Pesada")
    @Column(nullable = false, length = 20)
    private String categoria;

    // Preço em dólares do jogo
    @NotNull(message = "O preço é obrigatório")
    @Min(value = 0, message = "O preço não pode ser negativo")
    @Max(value = 10000, message = "O preço deve ser no máximo $10000")
    @Column(nullable = false)
    private Integer preco;

    // Lado que pode comprar: TR (terroristas), CT (contra-terroristas) ou Ambos
    @NotBlank(message = "O lado é obrigatório")
    @Pattern(regexp = "TR|CT|Ambos", message = "O lado deve ser TR, CT ou Ambos")
    @Column(nullable = false, length = 10)
    private String lado;

    // Dano base por disparo
    @NotNull(message = "O dano é obrigatório")
    @Min(value = 1, message = "O dano deve ser maior que zero")
    @Max(value = 500, message = "O dano deve ser no máximo 500")
    @Column(nullable = false)
    private Integer dano;

    // Construtor vazio exigido pelo JPA
    public Arma() {
    }

    // Construtor com todos os atributos
    public Arma(Long id, String nome, String categoria, Integer preco, String lado, Integer dano) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.lado = lado;
        this.dano = dano;
    }

    // Construtor sem o ID (usado para novos cadastros)
    public Arma(String nome, String categoria, Integer preco, String lado, Integer dano) {
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.lado = lado;
        this.dano = dano;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getPreco() {
        return preco;
    }

    public void setPreco(Integer preco) {
        this.preco = preco;
    }

    public String getLado() {
        return lado;
    }

    public void setLado(String lado) {
        this.lado = lado;
    }

    public Integer getDano() {
        return dano;
    }

    public void setDano(Integer dano) {
        this.dano = dano;
    }
}

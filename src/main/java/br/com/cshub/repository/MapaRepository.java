package br.com.cshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cshub.entity.Mapa;

// Acesso a dados da entidade Mapa
public interface MapaRepository extends JpaRepository<Mapa, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}

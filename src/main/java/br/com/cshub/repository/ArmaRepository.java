package br.com.cshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cshub.entity.Arma;

// Acesso a dados da entidade Arma
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}

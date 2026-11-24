package br.com.cshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cshub.entity.Time;

// Acesso a dados da entidade Time (o JpaRepository já traz findAll, findById, save, deleteById...)
public interface TimeRepository extends JpaRepository<Time, Long> {

    // Verifica se já existe um time com o mesmo nome (ignorando maiúsculas/minúsculas)
    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}

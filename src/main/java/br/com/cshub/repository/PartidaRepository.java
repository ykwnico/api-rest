package br.com.cshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cshub.entity.Partida;

// Acesso a dados da entidade Partida
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    // Usados para impedir a exclusão de times/mapas que já estão em partidas
    boolean existsByTimeAIdOrTimeBId(Long timeAId, Long timeBId);

    boolean existsByMapaId(Long mapaId);
}

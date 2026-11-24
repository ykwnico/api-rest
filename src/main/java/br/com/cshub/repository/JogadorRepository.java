package br.com.cshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cshub.entity.Jogador;

// Acesso a dados da entidade Jogador
public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByNicknameIgnoreCaseAndIdNot(String nickname, Long id);

    // Usado para impedir a exclusão de um time que ainda possui jogadores
    boolean existsByTimeId(Long timeId);
}

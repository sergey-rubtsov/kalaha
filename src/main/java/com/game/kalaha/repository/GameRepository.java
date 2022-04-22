package com.game.kalaha.repository;

import com.game.kalaha.model.Game;
import com.game.kalaha.model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findGameByUuid(UUID uuid);

    List<Game> findGamesByStatus(GameStatus status);

}

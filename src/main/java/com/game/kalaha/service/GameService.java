package com.game.kalaha.service;

import com.game.kalaha.model.Game;
import com.game.kalaha.model.GameStatus;

import java.util.List;
import java.util.UUID;

public interface GameService {

    Game createGame(UUID userUuid);

    Game joinGame(UUID userUuid, UUID gameUuid);

    Game getGame(UUID gameUuid);

    List<Game> findGames(GameStatus status);

    Game move(UUID userUuid, UUID gameUuid, Integer move);

}

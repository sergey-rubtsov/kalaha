package com.game.kalaha.controller;

import com.game.kalaha.messages.GameMessage;
import com.game.kalaha.messages.GameSessionMessage;
import com.game.kalaha.messages.translator.GameMessageTranslator;
import com.game.kalaha.model.GameStatus;
import com.game.kalaha.service.GameService;
import com.game.kalaha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class GameControllerImpl implements GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameMessageTranslator translator;

    public GameMessage status(UUID uuid) {
        return translator.toMessage(gameService.getGame(uuid));
    }

    public List<GameMessage> findGames(GameStatus status) {
        return gameService.findGames(status).stream()
                .map(g -> translator.toMessage(g))
                .collect(Collectors.toList());
    }

    public UUID createUser() {
        return userService.createUser().getUuid();
    }

    public UUID createGame(UUID userUuid) {
        return gameService.createGame(userUuid).getUuid();
    }

    public GameMessage joinGame(GameSessionMessage gameSessionMessage) {
        return translator.toMessage(gameService
                .joinGame(gameSessionMessage.getUser(), gameSessionMessage.getGame()));
    }

    public GameMessage move(GameSessionMessage gameSessionMessage, Integer move) {
        return translator.toMessage(gameService
                .move(gameSessionMessage.getUser(), gameSessionMessage.getGame(), move));
    }

}

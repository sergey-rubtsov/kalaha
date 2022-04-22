package com.game.kalaha.messages.translator;

import com.game.kalaha.messages.GameMessage;
import com.game.kalaha.messages.UserMessage;
import com.game.kalaha.model.Game;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameMessageTranslator {

    public GameMessage toMessage(Game game) {
        int[][] board = game.getBoard();
        List<UserMessage> players = game.getUsers().stream()
                .map(u -> UserMessage.builder().uuid(u.getUuid()).build())
                .collect(Collectors.toList());
        if (players.size() >= 1) {
            players.get(0).setStones(board[0][0]);
        }
        if (players.size() > 1) {
            players.get(1).setStones(board[1][board[1].length - 1]);
        }
        GameMessage.GameMessageBuilder builder = GameMessage.builder()
                .game(game.getUuid())
                .board(toMessage(board))
                .status(game.getStatus())
                .next(game.getNextPlayer())
                .players(players);
        return builder.build();
    }

    private int[][] toMessage(int[][] board) {
        int[][] boardMessage = new int[2][board[0].length - 1];
        System.arraycopy(board[0], 1, boardMessage[0], 0, board[0].length - 1);
        System.arraycopy(board[1], 0, boardMessage[1], 0, board[0].length - 1);
        return boardMessage;
    }

}

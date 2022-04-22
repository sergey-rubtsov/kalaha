package com.game.kalaha.service;

import com.game.kalaha.exception.BadRequestException;
import com.game.kalaha.exception.ForbiddenException;
import com.game.kalaha.exception.GameNotFoundException;
import com.game.kalaha.exception.UserNotFoundException;
import com.game.kalaha.model.Game;
import com.game.kalaha.model.GameStatus;
import com.game.kalaha.model.User;
import com.game.kalaha.repository.GameRepository;
import com.game.kalaha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Value("${pits:6}")
    private Integer pits;

    @Value("${stones:6}")
    private Integer stones;

    @Transactional
    public Game createGame(UUID userUuid) {
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(UserNotFoundException::new);
        Game game = Game.builder()
                .board(GameProcessor.boardSetup(pits, stones))
                .nextPlayer(user.getUuid())
                .build();
        gameRepository.save(game);
        user.getGames().add(game);
        game.getUsers().add(user);
        userRepository.save(user);
        return gameRepository.save(game);
    }

    @Transactional
    public Game joinGame(UUID userUuid, UUID gameUuid) {
        Game game = gameRepository.findGameByUuid(gameUuid)
                .orElseThrow(GameNotFoundException::new);
        if (game.getStatus() == GameStatus.ACTIVE) {
            throw new ForbiddenException("The game is already being played");
        } else if (game.getStatus() != GameStatus.NEW) {
            throw new ForbiddenException("Game over");
        }
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(UserNotFoundException::new);
        if (userUuid.equals(game.getUsers().get(0).getUuid())) {
            throw new ForbiddenException("This game is played by two players");
        }
        game.getUsers().add(user);
        game.setStatus(GameStatus.ACTIVE);
        userRepository.save(user);
        return gameRepository.save(game);
    }

    public List<Game> findGames(GameStatus status) {
        return gameRepository.findGamesByStatus(status);
    }

    public Game getGame(UUID gameUuid) {
        return gameRepository.findGameByUuid(gameUuid)
                .orElseThrow(GameNotFoundException::new);
    }

    @Transactional
    public Game move(UUID userUuid, UUID gameUuid, Integer move) {
        Game game = gameRepository.findGameByUuid(gameUuid)
                .orElseThrow(GameNotFoundException::new);
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(UserNotFoundException::new);
        processMove(game, user, move);
        return gameRepository.save(game);
    }

    private void processMove(Game game, User user, Integer move) {
        UUID currentUser = user.getUuid();
        if (!currentUser.equals(game.getNextPlayer())) {
            throw new ForbiddenException("Now it's your opponent's turn");
        }
        int[][] board = game.getBoard();
        if (move < 0 || move >= board[0].length) {
            throw new BadRequestException(String.format("Possible pit number is in the range %d:%d", 0, board[0].length - 1));
        }
        int row, col;
        User opponent;
        if (currentUser.equals(game.getUsers().get(0).getUuid())) {
            opponent = game.getUsers().get(1);
            row = 0;
            col = board[0].length - 1 - move;
        } else {
            opponent = game.getUsers().get(0);
            row = 1;
            col = move;
        }
        if (board[row][col] == 0) {
            throw new BadRequestException("Selected pit is empty");
        }
        if (GameProcessor.sow(board, row, col)) {
            game.setNextPlayer(opponent.getUuid());
        }
        if (GameProcessor.gameOver(board)) {
            int score = GameProcessor.compareScore(board);
            if (score > 0) {
                game.setStatus(GameStatus.BEGINNER_WON);
            } else if (score < 0) {
                game.setStatus(GameStatus.OPPONENT_WON);
            } else {
                game.setStatus(GameStatus.DRAW);
            }
        }
        game.setBoard(board);
    }

}

package com.game.kalaha.messages;

import com.game.kalaha.model.GameStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameMessage {

    private UUID game;

    private UUID next;

    private int[][] board;

    private List<UserMessage> players;

    private GameStatus status;

}

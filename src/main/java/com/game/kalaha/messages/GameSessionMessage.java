package com.game.kalaha.messages;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameSessionMessage {

    private UUID game;

    private UUID user;

}

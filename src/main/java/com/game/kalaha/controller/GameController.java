package com.game.kalaha.controller;

import com.game.kalaha.messages.GameMessage;
import com.game.kalaha.messages.GameSessionMessage;
import com.game.kalaha.model.GameStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RequestMapping(produces = "application/json")
public interface GameController {

    @GetMapping(path = {"/game/{uuid}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game status"),
            @ApiResponse(responseCode = "404", description = "The resource is not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    GameMessage status(@PathVariable UUID uuid);

    @GetMapping(path = {"/games/{status}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All games available to join"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    List<GameMessage> findGames(@PathVariable GameStatus status);

    @PostMapping(path = {"/user/new"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    UUID createUser();

    @PostMapping(path = {"/game/{uuid}/new"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    UUID createGame(@PathVariable("uuid") UUID userUuid);

    @PostMapping(path = {"/game/join"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accepted request"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource is not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    GameMessage joinGame(@RequestBody GameSessionMessage gameSessionMessage);

    @PostMapping(path = {"/game/{move}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accepted request"),
            @ApiResponse(responseCode = "404", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource is not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred")
    })
    GameMessage move(@RequestBody GameSessionMessage gameSessionMessage, @Min(0) @PathVariable Integer move);

}


package com.game.kalaha;

import com.game.kalaha.messages.GameMessage;
import com.game.kalaha.messages.GameSessionMessage;
import com.game.kalaha.model.Game;
import com.game.kalaha.model.GameStatus;
import com.game.kalaha.repository.GameRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class KalahaApplicationTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameRepository gameRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void successIntegrationTest() throws Exception {
        URI createBeginner = URI.create("/user/new");
        ResultActions mvcResult = this.mockMvc.perform(get(createBeginner)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID beginnerUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        mvcResult = this.mockMvc.perform(get(createBeginner)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID opponentUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        URI createGame = URI.create(String.format("/game/%s/new", beginnerUuid));
        this.mockMvc.perform(MockMvcRequestBuilders.post(createGame)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(createGame)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID gameUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        URI findGames = URI.create("/games/NEW");
        mvcResult = this.mockMvc.perform(get(findGames)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        List<GameMessage> messages = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(2, messages.size());
        URI getGame = URI.create(String.format("/game/%s", gameUuid));
        mvcResult = this.mockMvc.perform(get(getGame)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        GameMessage newGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        assertEquals(gameUuid, newGame.getGame());
        assertEquals(beginnerUuid, newGame.getPlayers().get(0).getUuid());
        assertEquals(0, newGame.getPlayers().get(0).getStones());
        assertEquals(beginnerUuid, newGame.getNext());
        assertEquals(1, newGame.getPlayers().size());
        int[][] initExpected = {
                {6, 6, 6, 6, 6, 6},
                {6, 6, 6, 6, 6, 6}
        };
        assertArrayEquals(initExpected, newGame.getBoard());
        GameSessionMessage opponentMessage = GameSessionMessage.builder()
                .game(gameUuid)
                .user(opponentUuid)
                .build();
        URI joinOpponent = URI.create("/game/join");
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isOk());
        GameMessage activeGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        assertEquals(gameUuid, activeGame.getGame());
        assertEquals(beginnerUuid, activeGame.getPlayers().get(0).getUuid());
        assertEquals(0, activeGame.getPlayers().get(0).getStones());
        assertEquals(beginnerUuid, activeGame.getNext());
        assertEquals(opponentUuid, activeGame.getPlayers().get(1).getUuid());
        assertEquals(0, activeGame.getPlayers().get(1).getStones());
        assertArrayEquals(initExpected, activeGame.getBoard());
        assertEquals(GameStatus.ACTIVE, activeGame.getStatus());
        assertEquals(2, activeGame.getPlayers().size());
        URI move5 = URI.create("/game/5");
        GameSessionMessage beginnerMessage = GameSessionMessage.builder()
                .game(gameUuid)
                .user(beginnerUuid)
                .build();
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isOk());
        activeGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        int[][] expected1 = {
                {0, 6, 6, 6, 6, 6},
                {7, 7, 7, 7, 7, 6}
        };
        assertArrayEquals(expected1, activeGame.getBoard());
        assertEquals(opponentUuid, activeGame.getNext());
        assertEquals(1, activeGame.getPlayers().get(0).getStones());
        URI move1 = URI.create("/game/1");
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(move1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isOk());
        activeGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        int[][] expected2 = {
                {0, 6, 6, 6, 7, 7},
                {7, 0, 8, 8, 8, 7}
        };
        assertArrayEquals(expected2, activeGame.getBoard());
        assertEquals(beginnerUuid, activeGame.getNext());
        assertEquals(1, activeGame.getPlayers().get(1).getStones());
        Game game = gameRepository.findGameByUuid(gameUuid).orElseThrow();
        int[][] finalBoardBeginnerWin = {
                {15, 1, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 10}
        };
        game.setBoard(finalBoardBeginnerWin);
        gameRepository.save(game);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isOk());
        GameMessage endedGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        assertEquals(GameStatus.BEGINNER_WON, endedGame.getStatus());
        assertEquals(16, endedGame.getPlayers().get(0).getStones());
        assertEquals(11, endedGame.getPlayers().get(1).getStones());
        int[][] finalBoardDraw = {
                {9, 1, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 9}
        };
        game.setBoard(finalBoardDraw);
        game.setStatus(GameStatus.ACTIVE);
        gameRepository.save(game);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isOk());
        endedGame = objectMapper.readValue(mvcResult.andReturn().getResponse().getContentAsString(),
                GameMessage.class);
        assertEquals(GameStatus.DRAW, endedGame.getStatus());
    }

    @Test
    void exceptionsIntegrationTest() throws Exception {
        URI createBeginner = URI.create("/user/new");
        ResultActions mvcResult = this.mockMvc.perform(get(createBeginner)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID beginnerUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        mvcResult = this.mockMvc.perform(get(createBeginner)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID opponentUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        URI createGame = URI.create(String.format("/game/%s/new", beginnerUuid));
        this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(String.format("/game/%s/new", UUID.randomUUID())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(createGame)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID gameUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        URI getGame = URI.create(String.format("/game/%s", gameUuid));
        this.mockMvc.perform(get(URI.create(String.format("/game/%s", UUID.randomUUID())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(get(getGame)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        URI joinOpponent = URI.create("/game/join");
        GameSessionMessage message = GameSessionMessage.builder()
                .game(UUID.randomUUID())
                .user(opponentUuid)
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNotFound());
        message = GameSessionMessage.builder()
                .game(gameUuid)
                .user(UUID.randomUUID())
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNotFound());
        GameSessionMessage beginnerMessage = GameSessionMessage.builder()
                .game(gameUuid)
                .user(beginnerUuid)
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isForbidden());
        GameSessionMessage opponentMessage = GameSessionMessage.builder()
                .game(gameUuid)
                .user(opponentUuid)
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isOk());
        URI move5 = URI.create("/game/5");
        message = GameSessionMessage.builder()
                .game(gameUuid)
                .user(UUID.randomUUID())
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNotFound());
        message = GameSessionMessage.builder()
                .game(UUID.randomUUID())
                .user(beginnerUuid)
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isForbidden());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isOk());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isForbidden());
        mvcResult = this.mockMvc.perform(get(createBeginner)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UUID thirdPlayerUuid = UUID.fromString(mvcResult.andReturn().getResponse().getContentAsString()
                .replace("\"", ""));
        GameSessionMessage thirdPlayerMessage = GameSessionMessage.builder()
                .game(gameUuid)
                .user(thirdPlayerUuid)
                .build();
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(thirdPlayerMessage)))
                .andExpect(status().isForbidden());
        URI move1 = URI.create("/game/1");
        URI impossibleMove = URI.create("/game/2000000000");
        this.mockMvc.perform(MockMvcRequestBuilders.post(impossibleMove)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isOk());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isForbidden());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isBadRequest());
        Game game = gameRepository.findGameByUuid(gameUuid).orElseThrow();
        int[][] finalBoard0 = {
                {5, 1, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 10}
        };
        ;
        game.setBoard(finalBoard0);
        gameRepository.save(game);
        this.mockMvc.perform(MockMvcRequestBuilders.post(move5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beginnerMessage)))
                .andExpect(status().isOk());
        this.mockMvc.perform(MockMvcRequestBuilders.post(move1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opponentMessage)))
                .andExpect(status().isForbidden());
        this.mockMvc.perform(MockMvcRequestBuilders.post(joinOpponent)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(thirdPlayerMessage)))
                .andExpect(status().isForbidden());
    }

}

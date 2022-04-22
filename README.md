# Kalaha game

This is web-based server implementation of Kalaha (or Mancala) game.

This implementation is very simplified, however, it allows you to fully play many games with many opponents. \
A temporary database is used to store the state of the game and isolate transactions.

Project uses Java, Spring Boot, JPA, H2 embedded database, Gradle and OpenAPI + Swagger as documentation tools for REST
API.

## How to run

Import project as Gradle project and run

```bash
gradle bootRun
```

## How to test the API

OpenAPI Swagger documentation available by address:
http://localhost:8080/swagger-ui/index.html

## How to play

This document does not describe the rules of the game, it is assumed that they are known. \
The server checks the correctness of the moves and enforces the rules.

The order of play is as follows:

* Players get UUID for authentication using the method:

```
curl -X 'POST' \
  'http://localhost:8080/user/new' \
  -H 'accept: application/json' \
  -d ''
```

* The player can then either create a game or join an existing game. When creating a game with status NEW, the player
  becomes a game starter. The game is creating with method, the response is game UUID:

```
curl -X 'POST' \
  'http://localhost:8080/game/{Player UUID}/new' \
  -H 'accept: application/json' \
  -d ''
```

* The player can get a list of existing games with specific status (possible statuses are NEW, ACTIVE, BEGINNER_WON,
  OPPONENT_WON, DRAW)
  with the method:

```
curl -X 'GET' \
  'http://localhost:8080/games/NEW' \
  -H 'accept: application/json'
```

The example response is next JSON:

```json
[
  {
    "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
    "next": "29a03271-935c-47b3-8e04-139431baf43f",
    "board": [
      [6, 6, 6, 6, 6, 6],
      [6, 6, 6, 6, 6, 6]
    ],
    "players": [
      {
        "uuid": "29a03271-935c-47b3-8e04-139431baf43f",
        "stones": 0
      }
    ],
    "status": "NEW"
  }
]
```

* The state of specific game can be found by game UUID with method:

```
curl -X 'GET' \
  'http://localhost:8080/game/{Game UUID}' \
  -H 'accept: application/json'
```

* When joining an existing game, the player must send a message with their UUID and game UUID to the available NEW game.

```json
{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "user": "0f904750-521f-4be1-805d-0d1092d1679d"
}
```

The method:

```
curl -X 'POST' \
  'http://localhost:8080/game/join' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "user": "0f904750-521f-4be1-805d-0d1092d1679d"
}'
```

Then the game status changes to ACTIVE:

```json
{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "next": "29a03271-935c-47b3-8e04-139431baf43f",
  "board": [
    [6, 6, 6, 6, 6, 6],
    [6, 6, 6, 6, 6, 6]
  ],
  "players": [
    {
      "uuid": "29a03271-935c-47b3-8e04-139431baf43f",
      "stones": 0
    },
    {
      "uuid": "0f904750-521f-4be1-805d-0d1092d1679d",
      "stones": 0
    }
  ],
  "status": "ACTIVE"
}
```

* After that, the player whose ID is specified in the "next" game field can move. To do this, the player has to send a
  message with player UUID and game UUID, as well as select an available move (by default, each player can choose a pit
  with a number from 0 to 6)

```
curl -X 'POST' \
  'http://localhost:8080/game/1' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "user": "29a03271-935c-47b3-8e04-139431baf43f"
}'
```

The response will contain the new state of game:

```json
{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "next": "0f904750-521f-4be1-805d-0d1092d1679d",
  "board": [
    [7, 7, 7, 7, 0, 6],
    [7, 6, 6, 6, 6, 6]
  ],
  "players": [
    {
      "uuid": "29a03271-935c-47b3-8e04-139431baf43f",
      "stones": 1
    },
    {
      "uuid": "0f904750-521f-4be1-805d-0d1092d1679d",
      "stones": 0
    }
  ],
  "status": "ACTIVE"
}
```

* After one player has made a move, the next player can move. They make moves until the game is over.

* After the game is over, the game changes its status to BEGINNER_WON, OPPONENT_WON or DRAW, depending on the score the
  players finished the game.

```json
{
  "game": "384641b8-6009-48ae-a4da-eb53ce35566f",
  "next": "0f904750-521f-4be1-805d-0d1092d1679d",
  "board": [
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0]
  ],
  "players": [
    {
      "uuid": "29a03271-935c-47b3-8e04-139431baf43f",
      "stones": 37
    },
    {
      "uuid": "0f904750-521f-4be1-805d-0d1092d1679d",
      "stones": 35
    }
  ],
  "status": "BEGINNER_WON"
}
```

## Tests

Integration and unit tests are implemented for testing. Code coverage is 98%
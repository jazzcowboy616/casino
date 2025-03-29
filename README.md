# Casino API

This is a simplified version of an implementation built with Spring Boot that serve a casino and provide features:;
- List games and their details
- Register a player
- Place a bet in a game as a player and returns the result
- Get the balance of the player
- Deposit money as a player

## Project Overview

You have been asked to build a secure and scalable RESTful API that can be read and maintain easily. 
It should cover all the functions described above and some other foundational requirements (Authentication, Data Consistency, etc)

## Technical Requirements

- Implement an API with Java.
- Use maven for dependencies and building. 
- Create unit tests to verify the APIs.
- Document the API, does not need to be very extensive.
- Necessary document and guide.
- Any open source framework or library.

## APIs

The Note API provides the following endpoints:
```
Authentication Endpoints
POST /api/auth/signup               create a new user account.
POST /api/auth/login                log in to an existing user account and receive an access token.

Player Endpoints
GET /api/players/{id}               get a player by id
PUT /api/players/{id}/deposit       make a random deposit for a player
GET /api/players/{id}/bet_summary   get the bet summary of a specific player

Game Endpoints
GET /api/games                          get a list of all games.
GET /api/games/search?query={query}     search for games based on keywords.
GET /api/games/{id}                     get a game by ID.
POST /api/games                         create a new game.
PUT /api/games/{id}                     update an existing game by ID.
POST /api/games/upload                  upload games though a xml file

Round Engpoints
GET /api/games/{id}/rounds/{roundId}    get a new round for a specific game
POST /api/games/{id}/rounds             create a new round for a specific game

Bet Endpoints
PUT /api/rounds/{id}                    Resolve a round of a game and settle all bets belongs to it.
POST /api/bets                          Place a bet for the player in a specific game round.
```

## Get Started

See the guide on getting started with the Casino API
[**here**](https://github.com/jazzcowboy616/casino/blob/main/QuickStarted.md).

## Test

**Linux:**
   ```bash
   cd ${application.home}
   ./mvn test
   ```

**Windows:**

   ```bash
   cd ${application.home}
   mvnw.cmd test
   ```

You can also test the API using a tool like Postman or curl.
I've attached postman collection json
[**here**](https://github.com/jazzcowboy616/casino/blob/main/Casino%20api.postman_collection.json).

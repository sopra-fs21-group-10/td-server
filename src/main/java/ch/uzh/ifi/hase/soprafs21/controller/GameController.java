package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to the game itself.
 * The controller will receive the request and delegate the execution to the GameService and finally return the result.
 */
@RestController
public class GameController {
    private final GameRepository gameRepository;
    private final GameService gameService;

    GameController(GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {

        // create game
        Long gameId = gameService.createGame(gamePostDTO.getPlayer1Id(), gamePostDTO.getPlayer2Id());

        return gameService.returnGameInformation(gameId);// return game-state == getGame
    }

    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable("gameId") long gameId) {

        // return state of game

        return gameService.returnGameInformation(gameId);
    }

}

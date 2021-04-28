package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGoldDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameMoveDTO;
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
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private final GameService gameService;

    GameController(GameRepository gameRepository,
                   GameService gameService,
                   UserRepository userRepository,
                   BoardRepository boardRepository) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
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
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable("gameId") long gameId) {

        // return state of game
        return gameService.returnGameInformation(gameId);
    }

    @PostMapping("/games/towers/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGoldDTO placeTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);// not sure if this returns an error or fails if no player was found

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.placeTower(payerBoard, gameMoveDTO.getCoordinates(), gameMoveDTO.getEntity()));

        return gameGoldDTO;// return game-state == getGame
    }

     @PatchMapping("/games/towers/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGoldDTO upgradeTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);// not sure if this returns an error or fails if no player was found

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.upgradeTower(payerBoard, gameMoveDTO.getCoordinates()));

        return gameGoldDTO;//
        }

    @DeleteMapping("/games/towers/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGoldDTO sellTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);// not sure if this returns an error or fails if no player was found

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.sellTower(payerBoard, gameMoveDTO.getCoordinates()));

        return gameGoldDTO;
    }
}

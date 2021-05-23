package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
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
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private final GameService gameService;

    GameController(GameService gameService,
                   UserRepository userRepository,
                   BoardRepository boardRepository) {
        this.gameService = gameService;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {

        // create game
        long gameId = gameService.createGame(gamePostDTO.getPlayer1Id(), gamePostDTO.getPlayer2Id());

        return gameService.returnGameInformation(gameId);// return game-state == getGame
    }

    // token is not being required because this way, the program could get extended to support a spectate mode
    // this method was more useful(like many),when multiplayer was still on the table
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable("gameId") long gameId) {
        // return state of game
        return gameService.returnGameInformation(gameId);
    }

    // after battle phase, return bool
    @PatchMapping("/games/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameContinueDTO updateGameState(@PathVariable("token") String token, @RequestBody GameUpdateDTO gameUpdateDTO) {
        User player = userRepository.findByToken(token);

        GameContinueDTO gameContinueDTO = new GameContinueDTO();// return bool if game should continue

        gameContinueDTO.setContinuing(gameService.updateGameState(player, gameUpdateDTO.getGold(), gameUpdateDTO.getHealth()));

        return gameContinueDTO;
    }

    @DeleteMapping("/games/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveGame(@PathVariable("token") String token) {
        User player = userRepository.findByToken(token);

        // delete game
        gameService.endGame(player);
    }

    @GetMapping("/games/battles/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameWaveDTO startBattlePhase(@PathVariable("token") String token) {
        User player = userRepository.findByToken(token);
        // add minions
        return gameService.startBattlePhase(player);
    }

    @PostMapping("/games/towers/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGoldDTO placeTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.placeTower(payerBoard, gameMoveDTO.getCoordinates(), gameMoveDTO.getPlayable()));

        return gameGoldDTO;// return game-state == getGame
    }

    @PatchMapping("/games/towers/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGoldDTO upgradeTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);
// not finding a board is not a problem(gets handled in upgradeTower)

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.upgradeTower(payerBoard, gameMoveDTO.getCoordinates()));

        return gameGoldDTO;//
    }

    @PatchMapping("/games/towers/sales/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGoldDTO sellTower(@PathVariable("token") String token, @RequestBody GameMoveDTO gameMoveDTO) {
        User player = userRepository.findByToken(token);

        Board payerBoard = boardRepository.findByOwner(player);

        GameGoldDTO gameGoldDTO = new GameGoldDTO();
        gameGoldDTO.setGold(gameService.sellTower(payerBoard, gameMoveDTO.getCoordinates()));

        return gameGoldDTO;
    }

    // when multiplayer was still part of the plan
    @PostMapping("/games/minions/{gameId}/{token}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGoldDTO buyMinion(@PathVariable("token") String token, @PathVariable("gameId") long gameId, @RequestBody GameMinionsPostDTO gameMinionsPostDTO) {
        GameGoldDTO gameGoldDTO = new GameGoldDTO();

        gameGoldDTO.setGold(gameService.buyMinion(token, gameId, gameMinionsPostDTO.getMinion()));

        return gameGoldDTO;// return game-state == getGame
    }
}

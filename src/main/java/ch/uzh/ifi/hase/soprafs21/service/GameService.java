package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.WeatherDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("boardRepository") BoardRepository boardRepository,
                       @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public long createGame(Long player1Id, Long player2Id) {
        User player1 = userRepository.getOne(player1Id);

        if(Objects.isNull(player1Id)|| player1==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }
        if(checkIfPlayerInGame(player1)){// player 1 already in game
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Players already in game");
        }

        if(Objects.isNull(player2Id)){// no player 2 == single player
            return createSinglePlayer(player1);
        }

        else{
            User player2 = userRepository.getOne(player2Id);
            if(player2==null){// player 2 id false
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player 2 not found");
            }
            if(checkIfPlayerInGame(player1)){// player 2 already in game
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Player 2 already in game");
            }
            //multiplayer game
            return createMultiPlayer(player1, player2);

        }
    }

    public GameGetDTO returnGameInformation(long gameId){
        Game game = gameRepository.getOne(gameId);
        if(Objects.isNull(gameId)|| game==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        Board player1Board = game.getPlayer1Board();

        if(player1Board==null){// no player 1 board
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Game board not found");
        }
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setPlayer1(returnPlayerState(player1Board, game));

        Board player2Board = game.getPlayer2Board();
        if(player2Board != null){//single player
            gameGetDTO.setPlayer2(returnPlayerState(player2Board, game));
        }
        return gameGetDTO;
    }

    private long createSinglePlayer(User player1){
        if(player1==null){// no player
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }

        Game game = new Game();

        game.setPlayer1Board(setBoard(player1));

        Game created = gameRepository.save(game);
        gameRepository.flush();

        return created.getGameId();
    }

    private long createMultiPlayer(User player1, User player2){
        if(player1==null || player2==null){// no player, should not happen but...
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }

        Game game = new Game();

        game.setPlayer1Board(setBoard(player1));
        game.setPlayer2Board(setBoard(player2));

        Game created = gameRepository.save(game);
        gameRepository.flush();

        return created.getGameId();
    }

    private Board setBoard(User player1){
        if(player1==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }
        Board playerBoard = new Board();
        playerBoard.setOwner(player1);
        playerBoard.setWeather(returnWeatherTypePlayer(player1));

        Board createdBoard = boardRepository.saveAndFlush(playerBoard);

        log.debug("Created Information for Board: {}", createdBoard);

        return playerBoard;
    }

    private HashMap<String, Object> returnPlayerState(Board board, Game game){
        if(board==null){// no board(should never happen but...)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }

        HashMap<String, Object> returnMapping = new HashMap<>();
        returnMapping.put("gold",board.getGold());
        returnMapping.put("health",board.getHealth());
        returnMapping.put("owner",board.getOwner().getUsername());
        returnMapping.put("gameId",game.getGameId());
        returnMapping.put("weather",returnWeatherTypePlayer(board.getOwner()));
        returnMapping.put("boardId",board.getBoardId());
        returnMapping.put("board", board.getBoard());
        return returnMapping;
    }

    /**
     * returns the weather type(Clouds..) from a given user by looking ath their location as string
     *
     * @param user user, whose weather shall be returned
     * @throws ResponseStatusException HTTP
     */
    public String returnWeatherTypePlayer(User user){
        if(user ==null){// id does not exist,   should never happen but...
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with userId was not found");
        }

        try {// location should always exist because it is checked before being entered, but for safety

            URL jsonUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q="+user.getLocation()+"&appid="+System.getenv("WeatherKey"));//last part is the key

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            WeatherDTO weather = mapper.readValue(jsonUrl, WeatherDTO.class);

            return weather.getWeather().get(0).get("main");
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid location or to many requests");
        }
    }

    private boolean checkIfPlayerInGame(User user){
        if (user==null){
            return false;//player does not exist
        }
        Board board = boardRepository.findByOwner(user);
        return board != null;//player does not exist
    }
}

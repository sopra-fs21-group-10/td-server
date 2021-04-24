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
    private final Logger log = LoggerFactory.getLogger(UserService.class);

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

    public GameGetDTO createGame(Long player1Id, Long player2Id) {
        User player1 = userRepository.getOne(player1Id);

        if(Objects.isNull(player1Id)|| player1==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }
        if(checkIfPlayerInGame(player1)){// player 1 already in game
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Players already in game");
        }

        if(Objects.isNull(player2Id)){// no player 2 == single player
            HashMap<String, Object> p1Map = createSinglePlayer(player1);
            GameGetDTO gameGetDTO = new GameGetDTO();
            gameGetDTO.setPlayer1(p1Map);

            return gameGetDTO;
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
            HashMap<String, HashMap<String, Object>> maps = createMultiPlayer(player1, player2);
            GameGetDTO gameGetDTO = new GameGetDTO();
            gameGetDTO.setPlayer1(maps.get("player1"));
            gameGetDTO.setPlayer2(maps.get("player2"));
            return gameGetDTO;

        }
    }

    public GameGetDTO returnGameInformation(long gameId){
        Game game = gameRepository.getOne(gameId);
        if(Objects.isNull(gameId)|| game==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setPlayer1(returnPlayerState(game.getPlayer1Board().getOwner()));

        if(game.getPlayer2Board() != null){//single player
            gameGetDTO.setPlayer2(returnPlayerState(game.getPlayer2Board().getOwner()));
        }
        return gameGetDTO;
    }

    private HashMap<String, Object> createSinglePlayer(User player1){
        if(player1==null){// no player
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }

        Game game = new Game();
        Board player1Board = new Board();
        player1Board.setOwner(player1);
        player1Board.setWeather(returnWeatherTypePlayer(player1));
        game.setPlayer1Board(player1Board);

        return returnPlayerState(player1);
    }

    private HashMap<String, HashMap<String, Object>> createMultiPlayer(User player1, User player2){
        if(player1==null || player2==null){// no player
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }

        Game game = new Game();
        Board player1Board = new Board();
        player1Board.setOwner(player1);
        player1Board.setWeather(returnWeatherTypePlayer(player1));
        game.setPlayer1Board(player1Board);

        HashMap<String, HashMap<String, Object>> playerMapping = new HashMap<>();
        playerMapping.put("player1",returnPlayerState(player1));
        playerMapping.put("player2",returnPlayerState(player2));
        return playerMapping;
    }

    private HashMap<String, Object> returnPlayerState(User player1){
        if(player1==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }

        Game game = new Game();
        Board player1Board = new Board();
        player1Board.setOwner(player1);
        player1Board.setWeather(returnWeatherTypePlayer(player1));
        game.setPlayer1Board(player1Board);

        HashMap<String, Object> returnMapping = new HashMap<>();
        returnMapping.put("gold",player1Board.getGold());
        returnMapping.put("health",player1Board.getHealth());
        returnMapping.put("owner",player1Board.getOwner().getUsername());
        returnMapping.put("gameId",game.getGameId());
        returnMapping.put("weather",returnWeatherTypePlayer(player1));
        returnMapping.put("boardId",player1Board.getBoardId());
        returnMapping.put("board", player1Board.getBoard());
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

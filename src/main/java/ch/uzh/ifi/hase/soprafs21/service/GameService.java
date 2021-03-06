package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameWaveDTO;
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
import java.util.*;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private static final Map<String, Integer> towerLevel1Map = new HashMap<>();
     static {//tower, cost
        towerLevel1Map.put("FireTower1", 300);
        towerLevel1Map.put("WaterTower1", 200);
        towerLevel1Map.put("PlantTower1",100);
        towerLevel1Map.put("PsychTower1",400);
        towerLevel1Map.put("DragonTower1",1000);
    }

    private static final Map<String, Integer> towerLevel2Map = new HashMap<>();
    static {//tower, cost
        towerLevel2Map.put("FireTower2", 600);
        towerLevel2Map.put("WaterTower2", 400);
        towerLevel2Map.put("PlantTower2",200);
        towerLevel2Map.put("PsychTower2",1000);
        towerLevel2Map.put("DragonTower2",2500);
    }

    private static final Map<String, Integer> towerLevel3Map = new HashMap<>();
    static {//tower, cost
        towerLevel3Map.put("FireTower3", 1500);
        towerLevel3Map.put("WaterTower3", 1000);
        towerLevel3Map.put("PlantTower3",500);
        towerLevel3Map.put("PsychTower3",5000);
        towerLevel3Map.put("DragonTower3",10000);
    }

    private static final Map<String, Integer> minionMap = new HashMap<>();
    static {//minion, cost
        minionMap.put("Karpador", 50);
        minionMap.put("Nebulak", 200);
        minionMap.put("Garados", 500);
        minionMap.put("Zapdos",5000);
        minionMap.put("Arktos",10000);
        minionMap.put("Lavados",15000);
        minionMap.put("Gengar",500);
    }

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

    /**
     * checks how many players and creates a game for the, end returns its id
     *
     * @param player1Id player 1, error if null
     * @param player2Id player2, can be null
     * @return long id of created game
     * @throws ResponseStatusException HTTP
     */
    public long createGame(Long player1Id, Long player2Id) {
        User player1 = userRepository.getOne(player1Id);

        if(Objects.isNull(player1Id)|| player1==null){// no player 1
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if(checkIfPlayerInGame(player1)){// player 1 already in game
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Player 1 already in game");
        }

        if(Objects.isNull(player2Id)){// no player 2 == single player
            return createSinglePlayer(player1);
        }

        else{
            User player2 = userRepository.getOne(player2Id);
           // player 2 id false testing player2==null => bug because getOne should always return stg.

            if(checkIfPlayerInGame(player1)){// player 2 already in game
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Player 2 already in game");
            }
            //multiplayer game
            return createMultiPlayer(player1, player2);
        }
    }

    /**
     * checks if game exists, and returns a representation of the game-state
     *
     * @param gameId game of interest
     * @return GameGetDTO == player1{game info}, player2{game info}
     * @throws ResponseStatusException HTTP
     */
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
        gameGetDTO.setPlayer1(returnPlayerState(player1Board));
        gameGetDTO.setGameId(gameId);
        gameGetDTO.setRound(game.getRound());

        Board player2Board = game.getPlayer2Board();
        if(player2Board != null){//single player
            gameGetDTO.setPlayer2(returnPlayerState(player2Board));
        }
        return gameGetDTO;
    }

    /**
     * Changes health and gold of player according to information given
     *
     * @param player player whose board is being changed
     * @param gold new gold of player
     * @param health new health of player
     */
    public boolean updateGameState(User player, int gold, int health){
        Board board = boardRepository.findByOwner(player);

        if(board==null){// no player 1 board
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        if (gold<0){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gold cannot be negative");
        }

        if(health < 1){// game over
            // don't need to check if player 2 because only single player

            Game game = gameRepository.findGameByPlayer1Board(board);

            // delete unused game to be able to create a new one
            gameRepository.delete(game);
            log.debug("deleted Game: {}", game);
            boardRepository.delete(board);
            log.debug("deleted board: {}", board);

            // +delete player 2 board if multi

            return false;
        }

        board.setGold(gold);
        board.setHealth(health);
        board.setMinions(new ArrayList<>());

        boardRepository.saveAndFlush(board);
        return true;
    }

    /**
     * ending the game, if the player leaves
     *
     * @param player user whose game is being deleted
     */
    public void endGame(User player){
        // very similar to update game, but making one method out of it would make this one very strange
        Board board = boardRepository.findByOwner(player);

        if(board==null){// no board found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        Game game = gameRepository.findGameByPlayer1Board(board);

        // delete unused game to be able to create a new one
        gameRepository.delete(game);
        log.debug("deleted Game: {}", game);
        boardRepository.delete(board);
        log.debug("deleted board: {}", board);
    }

    /**
     * an algorithm which decides which minions to spawn at which point in the game,
     * the minions get added to the minion map in the boards
     *
     * @param player of game where minions should spawn
     * @throws ResponseStatusException HTTP
     */
    public GameWaveDTO startBattlePhase(User player){
        /*
        this will get messy, with a lot of calculations,
        but it seems more simple than for example making a behaviour for every round
         */
        Board foundBoard = boardRepository.findByOwner(player);
        Game game = gameRepository.findGameByPlayer1Board(foundBoard);
        double interestRate = 1.1;

        if(game == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "game does not exist");
        }

        int round = game.getRound();
        List<Board> players = new ArrayList<>();// iterate over boards to fill in minions
        players.add(game.getPlayer1Board());
        if (game.getPlayer2Board()!=null){
            players.add(game.getPlayer2Board());
        }

        if (round == 10){// boss
            for (Board board : players ){
                addMinions(board, "Garados", 1);
            }
        }
        if (round == 15){// boss
            for (Board board : players ){
                addMinions(board, "Zapdos", 1);
                addMinions(board, "Garados", 2);
            }
        }

        if (round == 20){// boss
            for (Board board : players ){
                addMinions(board, "Arktos", 1);
                addMinions(board, "Garados", 3);
            }
        }
        if (round == 25){//boss
            for (Board board : players ){
                addMinions(board, "Lavados", 1);
                addMinions(board, "Garados", 4);
            }
        }

        if (round == 30){// boss
            for (Board board : players ){
                addMinions(board, "Lavados", 50);// game over
            }
        }

        if (round >4 && round <15&& round%2==0){
            for (Board board : players ){
                addMinions(board, "Nebulak", ((round)/2));
            }
        }

        for (Board board : players ){// always happens
            addMinions(board, round <11?"Karpador": round <18?"Nebulak":"Gengar", Math.min(5+2*round, 33));
            addMinions(board, "Garados", Math.max(((round-12)/2),0));

            getInterest(board, interestRate);
            shuffleMinionOrder(board);
        }

        // increasing round
        game.setRound(round + 1);
        gameRepository.saveAndFlush(game);

        // return
        GameWaveDTO gameWaveDTO = new GameWaveDTO();
        gameWaveDTO.setPlayer1Minions(players.get(0).getMinions());

        if (players.size() == 2){// if multi
            gameWaveDTO.setPlayer2Minions(players.get(1).getMinions());
        }

        return gameWaveDTO;
    }

    /**
     * Places a tower on the board and adjusts the gold of the owner
     *
     * @param board target board to place tower
     * @param coordinates where to place the tower inside board [0-9, 0-14]
     * @param towerName name of the type of tower which is to be placed
     * @return remaining gold after buying tower
     * @throws ResponseStatusException HTTP
     */
    public int placeTower(Board board, int[] coordinates, String towerName){
        // check board
        if (board==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }

        // check coordinates
        checkCoordinates(coordinates);// throws error if not valid

        // check valid tower
        if (! towerLevel1Map.containsKey(towerName)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tower not found");
        }

        //can I place / is there space
        if (board.getGameMap()[coordinates[0]][coordinates[1]] != null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tower is blocked/ cannot be placed there");
        }

        // can I pay for it?
        if (board.getGold() < towerLevel1Map.get(towerName)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient funds");
        }

        // pay / place
        String[][] newBoard = board.getGameMap();
        newBoard[coordinates[0]][coordinates[1]] = towerName;
        board.setGold(board.getGold() - towerLevel1Map.get(towerName));//pay
        board.setGameMap(newBoard);

        board = boardRepository.saveAndFlush(board);

        return board.getGold();
    }

    public int upgradeTower(Board board, int[] coordinates){
        // check board
        if (board==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        // check coordinates
        checkCoordinates(coordinates);// throws error if not valid

        String towerName = board.getGameMap()[coordinates[0]][coordinates[1]];

        //can I place / is there space
        if (towerLevel1Map.containsKey(towerName)){// tower lvl 1
            String upgraded = towerName.substring(0, towerName.length()-1)+"2";

            // can I pay for it?
            if (! towerLevel2Map.containsKey(upgraded)){// there exists no tower on this level
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tower not upgradeable");
            }
            int cost = towerLevel2Map.get(upgraded);
            if (board.getGold() < cost){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient funds");
            }
            // pay / place
            String[][] newBoard = board.getGameMap();
            newBoard[coordinates[0]][coordinates[1]] = upgraded;
            board.setGold(board.getGold() - cost);//pay
            board.setGameMap(newBoard);

            board = boardRepository.saveAndFlush(board);
            return board.getGold();
        }
        else if (towerLevel2Map.containsKey(towerName)){// tower level 2
            String upgraded = towerName.substring(0, towerName.length()-1)+"3";
            // can I pay for it?
            if (! towerLevel3Map.containsKey(upgraded)){// there exists no tower with this level
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tower not upgradeable");
            }
            int cost = towerLevel3Map.get(upgraded);
            if (board.getGold() < cost){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient funds");
            }
            // pay / place
            String[][] newBoard = board.getGameMap();
            newBoard[coordinates[0]][coordinates[1]] = upgraded;
            board.setGold(board.getGold() - cost);//pay
            board.setGameMap(newBoard);

            board = boardRepository.saveAndFlush(board);
            return board.getGold();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Upgrading not possible");
    }

    /**
     * sell a Tower and return the updated gold count
     *
     * @param board on which board the tower is
     * @param coordinates where the tower is
     * @return new gold count of player
     */
    public int sellTower(Board board, int[] coordinates){
        // check board

        double sellValue = 0.7;
        if (board==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }

        // check coordinates
        checkCoordinates(coordinates);// throws error if not valid

        String tower = board.getGameMap()[coordinates[0]][coordinates[1]];
        //can I place / is there space
        if (towerLevel1Map.containsKey(tower)){// tower lvl 1
            int cost = towerLevel1Map.get(tower);
            // pay / place
            String[][] newBoard = board.getGameMap();
            newBoard[coordinates[0]][coordinates[1]] = null;
            board.setGold((int)(board.getGold() + cost*sellValue));//get some money back
            board.setGameMap(newBoard);

            board = boardRepository.saveAndFlush(board);
            return board.getGold();
        }
        else if (towerLevel2Map.containsKey(tower)){// tower lvl 2
            int cost = towerLevel2Map.get(tower);
            // pay / place
            String[][] newBoard = board.getGameMap();
            newBoard[coordinates[0]][coordinates[1]] = null;
            board.setGold((int)(board.getGold() + cost*sellValue));//get some money back
            board.setGameMap(newBoard);

            board = boardRepository.saveAndFlush(board);
            return board.getGold();
        }
        else if (towerLevel3Map.containsKey(tower)){// tower lvl 3
            int cost = towerLevel3Map.get(tower);
            // pay / place
            String[][] newBoard = board.getGameMap();
            newBoard[coordinates[0]][coordinates[1]] = null;
            board.setGold((int)(board.getGold() + cost*sellValue));//get some money back
            board.setGameMap(newBoard);

            board = boardRepository.saveAndFlush(board);
            return board.getGold();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "could not sell");
    }

    /**
     * Checks if multiplayer and adds opponent to enemy extra minions
     *
     * @param token to identify the player
     * @param gameId see if there is an opponent
     * @param minionName of the minion being bought
     * @return remaining gold after buying tower
     * @throws ResponseStatusException HTTP
     */
    public int buyMinion(String token, long gameId, String minionName){
        User player = userRepository.findByToken(token);
        Board board = boardRepository.findByOwner(player);

        // check board
        if (board==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        Game game = gameRepository.getOne(gameId);

        if ( game.getPlayer2Board() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Single-player game (cannot buy minions)");
        }

        if ((board != game.getPlayer1Board()) && (board != game.getPlayer2Board())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not in game");
        }

        Board opponent = game.getPlayer1Board()==board ? game.getPlayer2Board() : game.getPlayer1Board();

        // check valid tower
        if (! minionMap.containsKey(minionName)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Minion not found");
        }

        int cost = minionMap.get(minionName);

        // can I pay for it?
        if (board.getGold() < cost){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient funds");
        }

        addMinions(opponent, minionName, 1);

        board.setGold(board.getGold() - cost);//pay

        board = boardRepository.saveAndFlush(board);

        return board.getGold();
    }

    /**
     * Checks if coordinates are valid(inside board...) or throws error
     *
     * @param coordinates int[]
     * @throws ResponseStatusException HTTP
     */
    private void checkCoordinates(int[] coordinates){
        if (coordinates.length !=2){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid coordinate size");
        }
        if (coordinates[0]>9 ||  coordinates[0]<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid coordinates");
        }
        if (coordinates[1]>14 ||  coordinates[1]<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid coordinates");
        }
    }

    /**
     * adds minions to map in board
     *
     * @param board board th which the minions should get added
     * @param minion minion to be added
     */
    private void addMinions(Board board, String minion, int number) {
        if (number <0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "negative number of minions");
        }
        /*i am assuming the board... is valid,
        adding to many ifs in methods that have been called in methods that already test the conditions is pointless
        /slows program down*/

        List<String> opponentExtraMinions = board.getMinions();

        // add minion to player
        for (int i = 0 ; i < number; i++){
            opponentExtraMinions.add(minion);
        }

        board.setMinions(opponentExtraMinions);

        boardRepository.saveAndFlush(board);
    }

    /**
     * randomizes the order of the minions
     * @param board where minions should get randomized
     */
    private void shuffleMinionOrder(Board board){
        List<String> toShuffle = board.getMinions();
        Collections.shuffle(toShuffle);
        board.setMinions(toShuffle);
        boardRepository.saveAndFlush(board);
    }

    /**
     * adds interest(gold) to map in board
     *
     * @param board board th which the gold should get added
     * @param interestRate eg 1.1
     */
    private void getInterest(Board board, double interestRate) {
        List<String> opponentExtraMinions = board.getMinions();

        board.setGold((int)(board.getGold() * interestRate));// 10% interest gained on start of battle phase
        boardRepository.saveAndFlush(board);
    }

    /**
     * creates a game with 1 player
     *
     * @param player1 owner of board 1
     * @return long gameId of created game
     * @throws ResponseStatusException HTTP
     */
    private long createSinglePlayer(User player1){
        if(player1==null){// no player
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }

        Game game = new Game();

        game.setPlayer1Board(setBoard(player1));

        Game created = gameRepository.save(game);
        gameRepository.flush();

        return created.getGameId();
    }

    /**
     * creates a game with 2 players
     *
     * @param player1 owner of board 1
     * @param player2 owner of board 2
     * @return long gameId of created game
     * @throws ResponseStatusException HTTP
     */
    private long createMultiPlayer(User player1, User player2){
        if(player1==null || player2==null){// no player, should not happen but...
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Players not found");
        }
        if(player1==player2){// no player, should not happen but...
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Players are the same");
        }

        Game game = new Game();

        game.setPlayer1Board(setBoard(player1));
        game.setPlayer2Board(setBoard(player2));

        Game created = gameRepository.save(game);
        gameRepository.flush();

        return created.getGameId();
    }

    /**
     * creates a board for the player
     *
     * @param player1 owner of board
     * @return created board
     * @throws ResponseStatusException HTTP
     */
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

    /**
     * returns a representation of a board-state (health, board-setup)
     *
     * @param board whose info is to be returned
     * @return map of game-state == {gold:int, health:int, owner: str, ...}
     * @throws ResponseStatusException HTTP
     */
    private HashMap<String, Object> returnPlayerState(Board board){
        if(board==null){// no board(should never happen but...)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }

        HashMap<String, Object> returnMapping = new HashMap<>();
        returnMapping.put("gold",board.getGold());
        returnMapping.put("health",board.getHealth());
        returnMapping.put("owner",board.getOwner().getUsername());
        returnMapping.put("weather",board.getWeather());
        returnMapping.put("boardId",board.getBoardId());
        returnMapping.put("board", board.getGameMap());
        // extra minions not anymore in this mapping as they get sent at the start of the battle phase

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
            URL jsonUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q="
                    +user.getLocation()+"&appid="+System.getenv("WeatherKey"));//last part is the key

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            WeatherDTO weather = mapper.readValue(jsonUrl, WeatherDTO.class);

            return weather.getWeather().get(0).get("main");
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid location or to many requests");
        }
    }

    /**
     * checks if user is already in a game
     *
     * @param user target to check
     * @return true if player in a game
     * @throws ResponseStatusException HTTP
     */
    private boolean checkIfPlayerInGame(User user){
        if (user==null){
            return false;//player does not exist
        }
        Board board = boardRepository.findByOwner(user);
        return board != null;//player does not exist
    }
}

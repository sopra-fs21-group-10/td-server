package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @InjectMocks
    private GameService gameService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private BoardRepository boardRepository;

    private User testUser;

    private User testUser2;

    private Board dummyBoard;

    private Game dummyGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setToken("token");
        testUser.setUserId(1L);
        testUser.setStatus(UserStatus.ONLINE);

        testUser2 = new User();
        testUser2.setPassword("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("token2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setUserId(2L);

        dummyBoard = new Board();
        dummyBoard.setOwner(testUser);
        dummyBoard.setWeather("Clouds");

        dummyGame = new Game();
        dummyGame.setGameId(1L);
        dummyGame.setPlayer1Board(dummyBoard);

        // when -> any object is being save in the userRepository -> return the dummy
        Mockito.when(userRepository.getOne(testUser.getUserId())).thenReturn(testUser);
        Mockito.when(boardRepository.saveAndFlush(Mockito.any())).thenReturn(dummyBoard);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);
    }

    @Test
    void createGame_validInputsSinglePlayer_success() {
        //given
        assertEquals(1L, testUser.getUserId());
        gameService.createGame(testUser.getUserId(), null);
    }

    @Test
    void createGame_validInputsMultiPlayer_success() {
        //given
        Mockito.when(userRepository.getOne(testUser2.getUserId())).thenReturn(testUser2);

        gameService.createGame(testUser.getUserId(), testUser2.getUserId());
    }

    @Test
    void createGame_invalidInputsSinglePlayer_throws() {
        //given
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testUser2.getUserId(), null));// cannot upgrade anymore
    }

    @Test
    void createGame_PlayerAlreadyInGame_throws() {
        //given
        gameService.createGame(testUser.getUserId(), null);// create game with user

        // makes checkIfPlayerInGame find a board
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);

        // new game with same user throws
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testUser.getUserId(), null));// cannot upgrade anymore
    }

    @Test
    void returnGameInformation_validInputs_success() {
        //given
        dummyGame.setPlayer1Board(dummyBoard);
        Mockito.when(gameRepository.getOne(dummyGame.getGameId())).thenReturn(dummyGame);
        GameGetDTO gameGetDTO = gameService.returnGameInformation(dummyGame.getGameId());

        assertNotNull(gameGetDTO.getPlayer1());
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(100, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(gameGetDTO.getPlayer1().get("owner"), testUser.getUsername());
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
        assertEquals(dummyGame.getGameId(), gameGetDTO.getGameId());
        assertEquals(1, gameGetDTO.getRound());
        assertTrue(gameGetDTO.getPlayer1().containsKey("extraMinions"));

        assertNull(gameGetDTO.getPlayer2());//player2
        // etc
    }

    @Test
    void returnGameInformation_validInputsMultiPlayer_success() {
        //given
        Board board2 = new Board();
        board2.setOwner(testUser2);
        board2.setWeather("Clouds");

        dummyGame.setPlayer1Board(dummyBoard);
        dummyGame.setPlayer2Board(board2);

        Mockito.when(gameRepository.getOne(dummyGame.getGameId())).thenReturn(dummyGame);
        GameGetDTO gameGetDTO = gameService.returnGameInformation(dummyGame.getGameId());

        // check if board info is correct
        assertNotNull(gameGetDTO.getPlayer1());
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(dummyGame.getGameId(), gameGetDTO.getGameId());
        assertEquals(1, gameGetDTO.getRound());
        assertEquals(100, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(gameGetDTO.getPlayer1().get("owner"), testUser.getUsername());
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
        assertTrue(gameGetDTO.getPlayer1().containsKey("extraMinions"));
        // etc

        assertNotNull(gameGetDTO.getPlayer2());
        assertEquals(50, gameGetDTO.getPlayer2().get("health"));
        assertEquals(100, gameGetDTO.getPlayer2().get("gold"));
        assertEquals(gameGetDTO.getPlayer2().get("owner"), testUser2.getUsername());
        assertNotNull(gameGetDTO.getPlayer2().get("weather"));
        assertTrue(gameGetDTO.getPlayer2().containsKey("extraMinions"));
    }

    //_____________________________tower tests_______________________________________
    @Test
    void placeTower_validInputs_success() {
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(200);
        int newGold = gameService.placeTower(dummyBoard, coordinates, "FireTower1");

        assertEquals(100, newGold);
    }

    @Test
    void placeTower_onPath_throwsException() {
        //given
        int[] coordinates = new int[]{0,1};
        dummyBoard.setGold(200);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void placeTower_onTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(200);
        // place 1. tower
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// tested

        //place 2. tower at same location
        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void placeTower_invalidCoordinates_throwsException() {
        //given
        int[] coordinates = new int[]{0,19};
        dummyBoard.setGold(200);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void placeTower_invalidCoordinates2_throwsException() {
        //given
        int[] coordinates = new int[]{-1,10};
        dummyBoard.setGold(200);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void placeTower_invalidTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(200);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "SuperGigaTower123"));// cannot upgrade anymore
    }

    @Test
    void placeTower_insufficientFunds_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(20);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void upgradeTowerTwice_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.upgradeTower(dummyBoard, coordinates);

        assertEquals(400, newGold); // 1000-100-200-300=400
    }

    @Test
    void upgradeTower_invalidTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        gameService.upgradeTower(dummyBoard, coordinates);

        gameService.upgradeTower(dummyBoard, coordinates);// already tested

        assertThrows(ResponseStatusException.class, () -> gameService.upgradeTower(dummyBoard, coordinates));// cannot upgrade anymore
    }

    @Test
    void upgradeTower_invalidLocationNoTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        int[] coordinates2 = new int[]{0,13};

        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        assertThrows(ResponseStatusException.class, () -> gameService.upgradeTower(dummyBoard, coordinates2));// cannot upgrade anymore
    }

    @Test
    void upgradeTower_insufficientFunds_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};

        dummyBoard.setGold(100);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        assertThrows(ResponseStatusException.class, () -> gameService.upgradeTower(dummyBoard, coordinates));// cannot upgrade anymore
    }

    @Test
    void sellTower_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        int newGold = gameService.sellTower(dummyBoard, coordinates);

        assertEquals(970, newGold); // 1000-100+.7*100
    }

    @Test
    void sellTower2_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested
        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.sellTower(dummyBoard, coordinates);


        assertEquals(840, newGold);
    }

    @Test
    void sellTower3_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested
        gameService.upgradeTower(dummyBoard, coordinates);
        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.sellTower(dummyBoard, coordinates);

        assertEquals(610, newGold);
    }

    @Test
    void sellTower_invalidLocationNoTower_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);

        assertThrows(ResponseStatusException.class, () -> gameService.sellTower(dummyBoard, coordinates));// cannot upgrade anymore
    }

    //___________________________minions_________________________________________
    @Test
    void buyMinion_validInputs_success() {
        //given
        Board board2 = new Board();
        dummyBoard.setGold(1000);
        dummyGame.setPlayer2Board(board2);

        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);
        Mockito.when(gameRepository.getOne(1L)).thenReturn(dummyGame);


        int newGold = gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Goblin");

        assertEquals(950, newGold);
        assertEquals(1, board2.getMinions().get("Goblin"));
    }

    @Test
    void buyMinions_validInputs_success() {
        //given
        Board board2 = new Board();// board of opponent
        dummyBoard.setGold(1000);
        dummyGame.setPlayer2Board(board2);

        // mock Repositories
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);
        Mockito.when(gameRepository.getOne(1L)).thenReturn(dummyGame);

        // buy minions
        gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Goblin");

        int newGold = gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Goblin");

        // check if minion count/ gold count is correct
        assertEquals(900, newGold);
        assertEquals(900, dummyBoard.getGold());
        assertEquals(2, board2.getMinions().get("Goblin"));
    }

    @Test
    void buyMinion_invalidInputs_throws() {
        //given
        Board board2 = new Board();
        dummyBoard.setGold(1000);
        dummyGame.setPlayer2Board(board2);

        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);
        Mockito.when(gameRepository.getOne(1L)).thenReturn(dummyGame);

        assertThrows(ResponseStatusException.class,
                () -> gameService.buyMinion(testUser.getToken(),
                        dummyGame.getGameId(),  "Goblindqwdqwd"));// cannot upgrade anymore

    }

    @Test
    void buyMinion_insufficientFunds_throws() {
        //given
        Board board2 = new Board();
        dummyBoard.setGold(0);
        dummyGame.setPlayer2Board(board2);

        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);
        Mockito.when(gameRepository.getOne(1L)).thenReturn(dummyGame);

        assertThrows(ResponseStatusException.class,
                () -> gameService.buyMinion(testUser.getToken(),
                        dummyGame.getGameId(),  "Goblin"));// cannot upgrade anymore

    }

    @Test
    void buyMinion_SinglePlayer_throws() {
        //given
        dummyBoard.setGold(1000);

        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);
        Mockito.when(gameRepository.getOne(1L)).thenReturn(dummyGame);

        assertThrows(ResponseStatusException.class,
                () -> gameService.buyMinion(testUser.getToken(),
                        dummyGame.getGameId(),  "goblin"));// cannot upgrade anymore

    }
}

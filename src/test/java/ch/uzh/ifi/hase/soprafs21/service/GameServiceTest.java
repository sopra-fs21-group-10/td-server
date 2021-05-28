package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameWaveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

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
     void setup() {
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
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(dummyGame);
    }

    @Test
    void createGame_validInputsSinglePlayer_success() {
        //given
        assertEquals(1L, testUser.getUserId());

        //when
        gameService.createGame(testUser.getUserId(), null);
    }

    @Test
    void createGame_validInputsMultiPlayer_success() {
        //given
        Mockito.when(userRepository.getOne(testUser2.getUserId())).thenReturn(testUser2);

        long gameId = gameService.createGame(testUser.getUserId(), testUser2.getUserId());
        assertEquals(1L, gameId);
    }

    @Test
    void createGame_invalidInputsSinglePlayer_throws() {
        //when
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testUser2.getUserId(), null));//user does not exist
    }

    @Test
    void createGame_invalidInputsSinglePlayerNoPlayer_throws() {
        //when
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(null, null));//user does not exist
    }

    @Test
    void createGame_invalidInputsSinglePlayerOnlyPlayer2_throws() {
        //when
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(null, testUser.getUserId()));//user does not exist
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
        Mockito.when(gameRepository.getOne(dummyGame.getGameId())).thenReturn(dummyGame);
        GameGetDTO gameGetDTO = gameService.returnGameInformation(dummyGame.getGameId());

        assertNotNull(gameGetDTO.getPlayer1());
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(500, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(gameGetDTO.getPlayer1().get("owner"), testUser.getUsername());
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
        assertEquals(dummyGame.getGameId(), gameGetDTO.getGameId());
        assertEquals(1, gameGetDTO.getRound());

        assertNull(gameGetDTO.getPlayer2());//player2
        // etc
    }

    @Test
    void returnGameInformation_validInputsMultiPlayer_success() {
        //given
        Board board2 = new Board();
        board2.setOwner(testUser2);
        board2.setWeather("Clouds");
        board2.setBoardId(2L);
        dummyGame.setPlayer2Board(board2);

        // when
        Mockito.when(gameRepository.getOne(dummyGame.getGameId())).thenReturn(dummyGame);
        GameGetDTO gameGetDTO = gameService.returnGameInformation(dummyGame.getGameId());

        // check if board info is correct
        assertNotNull(gameGetDTO.getPlayer1());
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(dummyGame.getGameId(), gameGetDTO.getGameId());
        assertEquals(1, gameGetDTO.getRound());
        assertEquals(500, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(gameGetDTO.getPlayer1().get("owner"), testUser.getUsername());
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
        // etc

        assertNotNull(gameGetDTO.getPlayer2());
        assertEquals(50, gameGetDTO.getPlayer2().get("health"));
        assertEquals(500, gameGetDTO.getPlayer2().get("gold"));
        assertEquals(gameGetDTO.getPlayer2().get("owner"), testUser2.getUsername());
        assertNotNull(gameGetDTO.getPlayer2().get("weather"));
    }

    @Test
    void returnGameInformation_noGame_throws() {
        // shows Objects.isNull(gameId)|| game==null   is not always false
        assertThrows(ResponseStatusException.class, () -> gameService.returnGameInformation(dummyGame.getGameId()));// board does not exist
    }

    @Test
    void updateGameState_validInputs_success() {
        //given
        Mockito.when( boardRepository.findByOwner(testUser)).thenReturn(dummyBoard);

        // when
        boolean continuing = gameService.updateGameState(testUser, 50, 1);

        // check if board info is correct
        assertEquals(50,dummyBoard.getGold());
        assertEquals(1,dummyBoard.getHealth());
        assertTrue(continuing);
    }

    @Test
    void updateGameState_noBoard_throw() {
        // given
        assertNull(boardRepository.findByOwner(testUser));
        // when
        assertThrows(ResponseStatusException.class, () -> gameService.updateGameState(testUser, 50, 1));// board does not exist
    }
    //_____________________________tower tests_______________________________________
    @Test
    void placeTower_validInputs_success() {
        // given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(400);

        // when
        int newGold = gameService.placeTower(dummyBoard, coordinates, "FireTower1");

        // then
        assertEquals(100, newGold);
    }

    @Test
    void placeTower_onPath_throwsException() {
        //given
        int[] coordinates = new int[]{0,1};
        dummyBoard.setGold(2000);

        // when
        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));// cannot upgrade anymore
    }

    @Test
    void placeTower_onTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(2000);
        // place 1. tower
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// tested

        //place 2. tower at same location
        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));
    }

    @Test
    void placeTower_invalidCoordinates_throwsException() {
        //given
        int[] coordinates = new int[]{0,19};
        dummyBoard.setGold(2000);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));
    }

    @Test
    void placeTower_invalidCoordinates2_throwsException() {
        //given
        int[] coordinates = new int[]{-1,10};
        dummyBoard.setGold(2000);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));
    }

    @Test
    void placeTower_invalidCoordinates3_throwsException() {
        //given
        int[] coordinates = new int[]{0,4,3};
        dummyBoard.setGold(2000);

        assertThrows(ResponseStatusException.class, () -> gameService.placeTower(dummyBoard, coordinates, "FireTower1"));
    }

    @Test
    void placeTower_invalidTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(2000);

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
        dummyBoard.setGold(2500);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.upgradeTower(dummyBoard, coordinates);

        assertEquals(100, newGold); // 1000-300-200-300=200
    }

    @Test
    void upgradeTower_invalidTower_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(3000);
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

        dummyBoard.setGold(10000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        assertThrows(ResponseStatusException.class, () -> gameService.upgradeTower(dummyBoard, coordinates2));// cannot upgrade anymore
    }

    @Test
    void upgradeTower_insufficientFunds_throwsException() {
        //given
        int[] coordinates = new int[]{0,14};

        dummyBoard.setGold(400);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        assertThrows(ResponseStatusException.class, () -> gameService.upgradeTower(dummyBoard, coordinates));
    }

    @Test
    void sellTower_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested

        int newGold = gameService.sellTower(dummyBoard, coordinates);

        assertEquals(910, newGold); // 1000-300+.7*300
    }

    @Test
    void sellTower2_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(1000);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested
        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.sellTower(dummyBoard, coordinates);

        assertEquals(520, newGold);// 1000-300-600=100, +.7*600
    }

    @Test
    void sellTower3_validInputs_success() {
        //given
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(2500);
        gameService.placeTower(dummyBoard, coordinates, "FireTower1");// already tested
        gameService.upgradeTower(dummyBoard, coordinates);
        gameService.upgradeTower(dummyBoard, coordinates);

        int newGold = gameService.sellTower(dummyBoard, coordinates);

        assertEquals(1150, newGold);//2000-300-600-1200=0  +1200*.7
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

        int newGold = gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Karpador");

        assertEquals(950, newGold);
        assertEquals(1, Collections.frequency(board2.getMinions(), "Karpador"));
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
        gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Karpador");

        int newGold = gameService.buyMinion(testUser.getToken(),dummyGame.getGameId(),  "Karpador");

        // check if minion count/ gold count is correct
        assertEquals(900, newGold);
        assertEquals(900, dummyBoard.getGold());
        assertEquals(2, Collections.frequency(board2.getMinions(), "Karpador"));
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
                        dummyGame.getGameId(),  "Karpador"));// cannot upgrade anymore
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
                        dummyGame.getGameId(),  "Karpador"));// cannot upgrade anymore
    }

    @Test
    void startBattlePhase_SinglePlayerWave1_success() {
        //given
        assertEquals(1, dummyGame.getRound());

        // mock Repositories

        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(2, dummyGame.getRound());
        assertEquals(7, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Karpador") );
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_MultilayerWave1_success() {
        //given
        assertEquals(1, dummyGame.getRound());
        dummyBoard.setGold(1000);
        Board board2 = new Board();
        dummyGame.setPlayer2Board(board2);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(2, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(7, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Karpador") );
        assertEquals(7, Collections.frequency(gameWaveDTO.getPlayer2Minions(), "Karpador") );
    }

    @Test
    void startBattlePhase_SinglePlayerWave5_success() {
        //given
        dummyGame.setRound(5);
        dummyBoard.setGold(1000);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(6, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(5+2*5, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Karpador") );
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_SinglePlayerWave10_success() {
        //given
        dummyGame.setRound(10);
        dummyBoard.setGold(1000);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(11, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(5+2*10, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Karpador"));
        assertEquals(1, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Garados"));
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_SinglePlayerWave15_success() {
        //given
        dummyGame.setRound(15);
        dummyBoard.setGold(1000);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(16, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(33, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Nebulak"));
        assertEquals(3, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Garados"));
        assertEquals(1, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Zapdos"));
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_SinglePlayerWave20_success() {
        //given
        dummyGame.setRound(20);
        dummyBoard.setGold(1000);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(21, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(33, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Gengar"));
        assertEquals(7, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Garados"));
        assertEquals(1, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Arktos"));
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_SinglePlayerWave25_success() {
        //given
        dummyGame.setRound(25);
        dummyBoard.setGold(1000);

        // mock Repositories
        Mockito.when(gameRepository.findGameByPlayer1Board(Mockito.any())).thenReturn(dummyGame);

        // design wave
        GameWaveDTO gameWaveDTO = gameService.startBattlePhase(testUser);

        // check if minion count/ gold count is correct
        assertEquals(26, dummyGame.getRound());
        assertEquals(1100, dummyBoard.getGold());
        assertEquals(33, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Gengar"));
        assertEquals(10, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Garados"));
        assertEquals(1, Collections.frequency(gameWaveDTO.getPlayer1Minions(), "Lavados"));
        assertNull(gameWaveDTO.getPlayer2Minions());
    }

    @Test
    void startBattlePhase_noGame_throws() {
        //  this shows that sonarcloud is wrong
        //  (it indicates that "game == null" is always false, which is not true
        assertThrows(ResponseStatusException.class,
                () -> gameService.startBattlePhase(testUser2));// cannot upgrade anymore
    }
}

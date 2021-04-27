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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameServiceTest {
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
        testUser.setId(1L);
        testUser.setStatus(UserStatus.ONLINE);

        testUser2 = new User();
        testUser2.setPassword("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("token2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setId(2L);

        dummyBoard = new Board();
        dummyBoard.setOwner(testUser);
        dummyBoard.setWeather("Clouds");

        dummyGame = new Game();
        dummyGame.setGameId(1L);
        dummyGame.setPlayer1Board(dummyBoard);

        // when -> any object is being save in the userRepository -> return the dummy
        Mockito.when(userRepository.getOne(testUser.getId())).thenReturn(testUser);
        Mockito.when(boardRepository.saveAndFlush(Mockito.any())).thenReturn(dummyBoard);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);
    }

    @Test
    void createGame_validInputsSinglePlayer_success() {
        gameService.createGame(testUser.getId(), null);
    }

    @Test
    void createGame_validInputsMultiPlayer_success() {
        Mockito.when(userRepository.getOne(testUser2.getId())).thenReturn(testUser2);

        gameService.createGame(testUser.getId(), testUser2.getId());
    }

    @Test
    void returnGameInformation_validInputs_success() {
        dummyGame.setPlayer1Board(dummyBoard);
        Mockito.when(gameRepository.getOne(dummyGame.getGameId())).thenReturn(dummyGame);
        GameGetDTO gameGetDTO = gameService.returnGameInformation(dummyGame.getGameId());

        assertNotNull(gameGetDTO.getPlayer1());
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(100, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(gameGetDTO.getPlayer1().get("owner"), testUser.getUsername());
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
        // etc
    }

    @Test
    void placeTower_validInputs_success() {
        int[] coordinates = new int[]{0,14};
        dummyBoard.setGold(200);
        int newGold = gameService.placeTower(dummyBoard, coordinates, "FireTower1");

        assertEquals(100, newGold);

    }
}

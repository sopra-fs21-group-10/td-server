package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GameResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
 class GameServiceIntegrationTest {
    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("boardRepository")
    @Autowired
    private BoardRepository boardRepository;

    private User testUser;

    private User testUser2;

    @BeforeEach
    void setup() {
        // order is important
        gameRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();

        // most/all tests need users
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testUser");
        testUser = userService.createUser(testUser);

        testUser2 = new User();
        testUser2.setUsername("testUser2");
        testUser2.setPassword("testUser2");
        testUser2 = userService.createUser(testUser2);
    }

    @Test
    void ReturnWeatherTypePlayer_validInputs_success() {
        // given -> a first user has already been created
        assertEquals("Zurich", testUser.getLocation());

        // ask Weather API
        gameService.returnWeatherTypePlayer(testUser);

        // the weather is not always the same so the test cant check for the right one
    }

    @Test
    void createMultiPlayer_MultiPlayer_success() {
        // check if Repositories empty
        assertTrue(gameRepository.findAll().isEmpty());
        assertNull(boardRepository.findByOwner(testUser));
        assertNull(boardRepository.findByOwner(testUser2));

        // create a multiplayer game
        gameService.createGame(testUser.getUserId(), testUser2.getUserId());

        // test if game/boards have been created
        assertFalse(gameRepository.findAll().isEmpty());
        assertNotNull(boardRepository.findByOwner(testUser));
        assertNotNull(boardRepository.findByOwner(testUser2));
    }

    @Test
    void createMultiPlayer_SinglePlayer_success() {
        assertTrue(gameRepository.findAll().isEmpty());
        assertNull(boardRepository.findByOwner(testUser));
        assertNull(boardRepository.findByOwner(testUser2));

        gameService.createGame(testUser.getUserId(), null);

        // test if game/boards have been created
        assertFalse(gameRepository.findAll().isEmpty());
        assertNotNull(boardRepository.findByOwner(testUser));
        assertNull(boardRepository.findByOwner(testUser2));
    }

    @Test
    void returnGameInformation_SinglePlayer_success() {
        // given
        Long gameId = gameService.createGame(testUser.getUserId(), null); //tested

        GameGetDTO gameGetDTO = gameService.returnGameInformation(gameId);

        assertEquals(500, gameGetDTO.getPlayer1().get("gold"));
        assertEquals(50, gameGetDTO.getPlayer1().get("health"));
        assertEquals(testUser.getUsername(), gameGetDTO.getPlayer1().get("owner"));
        assertEquals(gameId, gameGetDTO.getGameId());
        assertEquals(1, gameGetDTO.getRound());
        assertEquals(boardRepository.findByOwner(testUser).getBoardId(), gameGetDTO.getPlayer1().get("boardId"));
        // do not want to test weather, it has been tested before and the calls are limited, checking for same board is uggly
        assertNotNull(gameGetDTO.getPlayer1().get("board"));
        assertNotNull(gameGetDTO.getPlayer1().get("weather"));
    }

    @Test
    void updateGameState_SinglePlayerNoHeathLeft_success() {
        // given
        gameService.createGame(testUser.getUserId(), null); //tested
        assertNotNull(boardRepository.findByOwner(testUser));

        // when
        boolean continuing = gameService.updateGameState(testUser, 700, -3);

        // then
        assertEquals(new ArrayList<>(), boardRepository.findAll());
        assertEquals(new ArrayList<>(), gameRepository.findAll());
        assertFalse(continuing);
    }

    @Test
    void createMultiPlayer_SameUser_throwsException() {
        assertThrows(ResponseStatusException.class,
                () -> gameService.createGame(testUser.getUserId(), testUser.getUserId()));
    }

    @Test
    void endGame_SinglePlayer_success() {
        // given
        gameService.createGame(testUser.getUserId(), null); //tested
        assertNotNull(boardRepository.findByOwner(testUser));

        // when
        gameService.endGame(testUser);

        // then
        assertEquals(new ArrayList<>(), boardRepository.findAll());
        assertEquals(new ArrayList<>(), gameRepository.findAll());
    }
}

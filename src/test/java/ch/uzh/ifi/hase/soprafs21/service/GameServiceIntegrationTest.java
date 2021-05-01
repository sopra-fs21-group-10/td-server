package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        gameRepository.deleteAll();
        boardRepository.deleteAll();

        // most/all tests need an user
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testUser");
        testUser = userService.createUser(testUser);// tested
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
    void createMultiPlayer_SameUser_throwsException() {
        assertThrows(ResponseStatusException.class,
                () -> gameService.createGame(testUser.getUserId(), testUser.getUserId()));
    }
}

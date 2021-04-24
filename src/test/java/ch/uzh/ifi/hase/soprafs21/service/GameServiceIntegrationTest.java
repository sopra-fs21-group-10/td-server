package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the GameResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
 class GameServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameService gameService;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("boardRepository")
    @Autowired
    private BoardRepository boardRepository;

    @Test
    void ReturnWeatherTypePlayer_validInputs_success() {
        // given -> a first user has already been created
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        assertEquals("Zurich", testUser.getLocation());

        // when -> setup additional mocks for UserRepository

        gameService.returnWeatherTypePlayer(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        // the weather is not always the same so the test cant check for the right one
    }
}

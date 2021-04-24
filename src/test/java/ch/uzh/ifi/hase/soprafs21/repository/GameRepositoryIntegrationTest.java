package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    void findByOwner_success() {
        // given
        User user = new User();
        user.setPassword("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        entityManager.persist(user);
        entityManager.flush();

        Game game = new Game();
        entityManager.persist(game);
        entityManager.flush();

        Board board = new Board();
        board.setWeather("Clouds");
        board.setOwner(user);
        board.setGold(100);
        board.setHealth(50);
        board.setGame(game);
        // don't set board because default val

        entityManager.persist(board);
        entityManager.flush();

        // when
        Board found = boardRepository.findByOwner(user);

        // then
        assertNotNull(found.getBoardId());
        assertEquals(found.getGame(), board.getGame());
        assertEquals(found.getBoard(), board.getBoard());
        assertEquals(found.getGold(), board.getGold());
        assertEquals(found.getHealth(), board.getHealth());
        assertEquals(found.getOwner(), board.getOwner());
        assertEquals(found.getWeather(), board.getWeather());
    }
}

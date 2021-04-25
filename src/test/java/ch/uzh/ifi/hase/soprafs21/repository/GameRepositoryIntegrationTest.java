package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
 class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TestEntityManager entityManager2;

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
        user = entityManager.persist(user);
        entityManager.flush();
        System.out.println(user);

        Board board = new Board();
        board.setWeather("Clouds");
        board.setOwner(user);

        entityManager2.persist(board);
        entityManager2.flush();
//        // don't set board because default val

        // when
        Board found = boardRepository.findByOwner(user);

//         then
        assertNotNull(found.getBoardId());
        assertEquals(found.getBoard(), board.getBoard());
        assertEquals(found.getGold(), board.getGold());
        assertEquals(found.getHealth(), board.getHealth());
        assertEquals(found.getOwner(), board.getOwner());
        assertEquals(found.getWeather(), board.getWeather());
    }
}

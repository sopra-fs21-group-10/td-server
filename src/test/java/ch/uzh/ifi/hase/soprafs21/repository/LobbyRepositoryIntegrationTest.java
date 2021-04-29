package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerLobbyStatus;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class LobbyRepositoryIntegrationTest {
    @Autowired TestEntityManager entityManager;

    @Autowired
    private LobbyRepository lobbyRepository;


    @Test
    void findLobbyByOwner_sucess(){
        //given
        Lobby lobby = new Lobby();
        User user = new User();
        user.setPassword("password1");
        user.setUsername("username1");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        User user2 = new User();
        user2.setPassword("password2");
        user2.setUsername("username2");
        user2.setStatus(UserStatus.OFFLINE);
        user2.setToken("2");

        lobby.setOwner(user);
        lobby.setPlayer2(user2);
        lobby.setLobbyStatus(PlayerLobbyStatus.READY);
        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(lobby);
        entityManager.flush();

        //when
        Lobby found = lobbyRepository.findLobbyByOwner(user);

        //then
        assertNotNull(found.getLobbyId());
        assertEquals(user, found.getOwner());
        assertEquals(PlayerLobbyStatus.READY, found.getLobbyStatus());
        assertEquals(user2, found.getPlayer2());

    }

    @Test
    void findLobbyByLobbyId_sucess(){
        //given
        Lobby lobby = new Lobby();
        User user = new User();
        user.setPassword("password1");
        user.setUsername("username1");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        User user2 = new User();
        user2.setPassword("password2");
        user2.setUsername("username2");
        user2.setStatus(UserStatus.OFFLINE);
        user2.setToken("2");
        lobby.setOwner(user);
        lobby.setPlayer2(user2);
        lobby.setLobbyStatus(PlayerLobbyStatus.READY);
        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(lobby);
        entityManager.flush();
        Long lobbyId = lobby.getLobbyId();

        //when
        Lobby found = lobbyRepository.findLobbyByLobbyId(lobbyId);

        //then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getOwner(), user);
        assertEquals(found.getLobbyStatus(),PlayerLobbyStatus.READY);
        assertEquals(found.getPlayer2(),user2);

    }
}

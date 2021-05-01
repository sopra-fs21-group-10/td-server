package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setToken("token");
        testUser.setStatus(UserStatus.OFFLINE);

        testUser2 = new User();
        testUser2.setUserId(2L);
        testUser2.setPassword("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("token2");
        testUser2.setStatus(UserStatus.OFFLINE);
    }

    @Test
    void create_Lobby_success(){
        Lobby testLobby = new Lobby();
        testLobby.setOwner(testUser);
        //mock repo methods
        Mockito.when(lobbyRepository.findLobbyByOwner(testUser)).thenReturn(null);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        //call the method to be tested
        Long lobbyId= lobbyService.createLobby(testUser);
        assertEquals(lobbyId, testLobby.getLobbyId());

    }

    @Test
    void create_Lobby_invalidInput_userIsAlreadyOwner(){
        //Mock repo method
        Mockito.when(lobbyRepository.findLobbyByOwner(testUser)).thenReturn(new Lobby());
        //test if exception is thrown
        assertThrows(ResponseStatusException.class, () -> lobbyService.createLobby(testUser));


    }

    @Test
    void findLobbyById_invalidInput_lobbyNotExists_throwsException(){
        //mock method of repo
        Lobby returnedLobby = new Lobby();
        returnedLobby.setLobbyId(1L);
        returnedLobby.setOwner(testUser);
        //mock method of repo
        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(null);
        //check if correct id is returned
        assertThrows(ResponseStatusException.class,()-> lobbyService.findLobbyById(1L));
    }

    @Test
    void addUserToLobby_invalidInput_lobbyFull_throwsException(){
        Lobby foundLobby = new Lobby();
        foundLobby.setOwner(testUser2);
        foundLobby.setPlayer2(testUser);
        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(foundLobby);
        assertThrows(ResponseStatusException.class,()-> lobbyService.addUserToLobby(Mockito.anyLong(),testUser));
    }

    @Test
    void addUserToLobby_success(){
        Lobby foundLobby = new Lobby();
        foundLobby.setOwner(testUser2);

        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(foundLobby);

        Lobby returnedLobby = lobbyService.addUserToLobby(Mockito.anyLong(),testUser);

        assertEquals(testUser,returnedLobby.getPlayer2());
    }

    @Test
    void getLobbies_success(){
        // given
        Lobby foundLobby = new Lobby();
        List<Lobby> returned = new ArrayList<>();
        returned.add(foundLobby);

        // mock repo
        Mockito.when(lobbyRepository.findAll()).thenReturn(returned);

        List<Lobby> found = lobbyService.getLobbies();

        assertEquals(returned,found);
    }

    @Test
    void deletePlayer2FromLobby(){
        //prepare lobby to be modified
        Lobby foundLobby = new Lobby();
        foundLobby.setLobbyId(1L);
        foundLobby.setOwner(testUser);
        foundLobby.setPlayer2(testUser2);
        //method is already tested
        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(foundLobby);

        //call the method
        Lobby returnedLobby = lobbyService.deleteUserFromLobby(2L,testUser2);
        //check if lobby update was correct
        assertNull(returnedLobby.getPlayer2());
    }

    @Test
    void deleteHostFromLobby(){
        //prepare lobby to be modified
        Lobby foundLobby = new Lobby();
        foundLobby.setLobbyId(1L);
        foundLobby.setOwner(testUser);
        foundLobby.setPlayer2(testUser2);
        //method is already tested
        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(foundLobby);

        //call the method
        lobbyService.deleteUserFromLobby(2L,testUser);
        //hard to test the lobbyrepository.delete(lobby)statement so test is just that no error is thrown
    }

    @Test
    void deletePlayerFromLobbyThatDoesNotExistsInLobby(){
        //prepare lobby to be modified
        User testUser3 = new User();
        Lobby foundLobby = new Lobby();
        foundLobby.setLobbyId(1L);
        foundLobby.setOwner(testUser);
        foundLobby.setPlayer2(testUser2);
        //method is already tested
        Mockito.when(lobbyRepository.findLobbyByLobbyId(Mockito.any())).thenReturn(foundLobby);

        // call method and catch error
        assertThrows(ResponseStatusException.class,()-> lobbyService.deleteUserFromLobby(2L,testUser3));
    }
}

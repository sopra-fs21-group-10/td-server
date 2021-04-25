package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerLobbyStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Lobby Service
 * This class is the "worker" and responsible for all functionality related to the Lobby
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public Long createLobby(User owner){
        Lobby temp = lobbyRepository.findLobbyByOwner(owner);
        if(temp!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"a lobby owner can only have one lobby at a time");
        }
        Lobby newLobby = new Lobby();
        newLobby.setOwner(owner);
        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();

        log.debug("Created Lobby for User: {}", owner);
        return newLobby.getLobbyId();
    }
    public List<Lobby> getLobbies(){
        return this.lobbyRepository.findAll();
    }
    public Lobby findLobbyById(Long lobbyId){
        Lobby lobbyById = lobbyRepository.findLobbyByLobbyId(lobbyId);
        if (lobbyById==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"the lobbyId provided is invalid/ doesn't exist");
        }
        return lobbyById;
    }
    public Lobby addUserToLobby(Long lobbyId,User userToBeAdded){
        Lobby lobby = findLobbyById(lobbyId);
        if(lobby.getPlayer2()!=null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"The lobby is full, a new player can not join");
        }
        lobby.setPlayer2(userToBeAdded);
        lobby.setLobbyStatus(PlayerLobbyStatus.WAITING);
        lobbyRepository.saveAndFlush(lobby);
        return lobby;
    }
    public Lobby deleteUserFromLobby(Long lobbyId, User userToBeRemoved){
        Lobby lobby = findLobbyById(lobbyId);
        if(lobby.getPlayer2()==userToBeRemoved){
            lobby.setPlayer2(null);
            lobby.setLobbyStatus(PlayerLobbyStatus.WAITING);
            lobbyRepository.saveAndFlush(lobby);
        }
        else {
        lobbyRepository.delete(lobby);
        lobbyRepository.flush();
        }
        return lobby;
    }
}

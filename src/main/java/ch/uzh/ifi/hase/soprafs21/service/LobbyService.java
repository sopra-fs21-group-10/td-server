package ch.uzh.ifi.hase.soprafs21.service;

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

    public Long create_lobby(User owner){
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
}

package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to the Lobby.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class LobbyController {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final LobbyService lobbyService;

    LobbyController(LobbyRepository lobbyRepository, UserService userService, LobbyService lobbyService) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyService = lobbyService;
    }
    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyPostDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API user to internal representation
        //User userInput = DTOMapper.INSTANCE.convertUserPostInDTOtoEntity(userInPostDTO);
        User lobbyOwner = userService.checkIfUserExistById(lobbyPostDTO.getId());
        Long createdLobbyId = lobbyService.create_lobby(lobbyOwner);
        // create user
        //User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntitytoLobbyPostDTO(createdLobbyId);
    }

}

package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbiesGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyByIdGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserUserIdTokenPatchDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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


    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbiesGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbiesGetDTO> lobbiesGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Lobby lobby : lobbies) {
            LobbiesGetDTO temp = new LobbiesGetDTO();
            temp.setLobbyId(lobby.getLobbyId());
            temp.setOwnerName(lobby.getOwner().getUsername());
            lobbiesGetDTOs.add(temp);
        }
        return lobbiesGetDTOs;
    }
    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyPostDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API user to internal representation
        //User userInput = DTOMapper.INSTANCE.convertUserPostInDTOtoEntity(userInPostDTO);
        User lobbyOwner = userService.checkIfUserExistById(lobbyPostDTO.getId());
        Long createdLobbyId = lobbyService.createLobby(lobbyOwner);
        // create user
        //User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntitytoLobbyPostDTO(createdLobbyId);
    }

    @GetMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyByIdGetDTO GetLobbyWithId(@PathVariable("lobbyId") Lobby lobbyId, @RequestBody LobbyPostDTO lobbyPostDTO) {
       Lobby lobbyById = lobbyService.findLobbyById(lobbyPostDTO.getId());
       LobbyByIdGetDTO lobbyByIdGetDTO = new LobbyByIdGetDTO();
       lobbyByIdGetDTO.setLobbyOwner(lobbyById.getOwner().getUsername());
       if(lobbyById.getPlayer2()==null){
           lobbyByIdGetDTO.setPlayer2("");
           lobbyByIdGetDTO.setPlayer2Status("");
       }
       else {
           lobbyByIdGetDTO.setPlayer2(lobbyById.getPlayer2().getUsername());
           lobbyByIdGetDTO.setPlayer2Status(lobbyById.getPlayer2().getStatus().toString());
       }
       return lobbyByIdGetDTO;
    }
}

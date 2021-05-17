package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerLobbyStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbiesGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyByIdGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyIdDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.TokenDTO;
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
    private final UserService userService;
    private final LobbyService lobbyService;

    LobbyController(UserService userService, LobbyService lobbyService) {
        this.userService = userService;
        this.lobbyService = lobbyService;
    }

    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbiesGetDTO> getAllLobbies() {
        // fetch all users in the internal representation
        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbiesGetDTO> lobbiesGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Lobby lobby : lobbies) {
            LobbiesGetDTO temp = new LobbiesGetDTO();
            temp.setLobbyId(lobby.getLobbyId());
            temp.setLobbyOwner(lobby.getOwner().getUsername());
            lobbiesGetDTOs.add(temp);
        }
        return lobbiesGetDTOs;
    }
    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyIdDTO createLobby(@RequestBody TokenDTO tokenDTO) {
        // convert API user to internal representation
        User lobbyOwner = userService.checkIfUserExistByToken(tokenDTO.getToken());

        Long createdLobbyId = lobbyService.createLobby(lobbyOwner);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyIdDTO(createdLobbyId);
    }

    @GetMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyByIdGetDTO getLobbyWithId(@PathVariable("lobbyId") Long lobbyId) {
       Lobby lobbyById = lobbyService.findLobbyById(lobbyId);
       LobbyByIdGetDTO lobbyByIdGetDTO = new LobbyByIdGetDTO();
       lobbyByIdGetDTO.setLobbyOwner(lobbyById.getOwner().getUsername());
       if(lobbyById.getPlayer2()==null){
           lobbyByIdGetDTO.setPlayer2("");
           lobbyByIdGetDTO.setPlayer2Status(PlayerLobbyStatus.WAITING.toString());
       }
       else {
           lobbyByIdGetDTO.setPlayer2(lobbyById.getPlayer2().getUsername());
           lobbyByIdGetDTO.setPlayer2Status(lobbyById.getLobbyStatus().toString());
       }
       return lobbyByIdGetDTO;
    }

    @PatchMapping("lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyByIdGetDTO joinALobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody TokenDTO lobbyPutAndPatchDTO){
        //check if player exists
        User userToBeAdded = userService.checkIfUserExistByToken(lobbyPutAndPatchDTO.getToken());

        //check if lobby is full
        lobbyService.addUserToLobby(lobbyId,userToBeAdded);
        //return infos about updated Lobby
        Lobby lobbyById = lobbyService.findLobbyById(lobbyId);
        LobbyByIdGetDTO lobbyByIdGetDTO = new LobbyByIdGetDTO();
        lobbyByIdGetDTO.setPlayer2(lobbyById.getPlayer2().getUsername());
        lobbyByIdGetDTO.setLobbyOwner(lobbyById.getOwner().getUsername());
        lobbyByIdGetDTO.setPlayer2Status(lobbyById.getLobbyStatus().toString());

        return lobbyByIdGetDTO;
    }

    @PutMapping ("lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveALobby(@PathVariable("lobbyId")Long lobbyId, @RequestBody TokenDTO lobbyPutAndPatchDTO){
        User userToBeRemoved = userService.checkIfUserExistByToken(lobbyPutAndPatchDTO.getToken());
        //check if is host
        //--> delete lobby if host leaves else delete player2 from lobby
        lobbyService.deleteUserFromLobby(lobbyId, userToBeRemoved);
    }
}

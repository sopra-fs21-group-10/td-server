package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostInDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserUserIdTokenPatchDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            if(user.getStatus()== UserStatus.ONLINE){
                userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
        }
        return userGetDTOs;
    }

    @PutMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPostDTO login(@RequestBody UserPostInDTO userPostInDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostInDTOtoEntity(userPostInDTO);

        User loggedIn = userService.userIn(userInput);

        return DTOMapper.INSTANCE.convertEntitytoUserPostDTO(loggedIn);
    }

    @PatchMapping("/users")//patch allowed??
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logout(@RequestBody UserPostDTO token) {

        User found = userRepository.findByToken(token.getToken());

        userService.userLogout(found);
    }

    @PatchMapping("/users/{userid}/{token}")//patch allowed??
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void changeData(@PathVariable("userId") Long userId, @PathVariable("token") String token,@RequestBody UserUserIdTokenPatchDTO userUserIdTokenPatchDTO) {

        User found = userRepository.getOne(userId);

        // did not want to convert input to user because some not nullable attributes are allowed to be null here
        // (maybe we only want to change location)
        userService.editProfile(found, token,
                userUserIdTokenPatchDTO.getUsername(),
                userUserIdTokenPatchDTO.getPassword(),
                userUserIdTokenPatchDTO.getLocation());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserPostDTO createUser(@RequestBody UserPostInDTO userInPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostInDTOtoEntity(userInPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntitytoUserPostDTO(createdUser);
    }
}

package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    /**
     * Creates an user with given username/password
     *
     * @param newUser
     * @return newUser
     * @throws ResponseStatusException
     */
    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * Checks if userinput is valid, and performs changes if valid
     *
     * @param user
     * @return found
     * @throws ResponseStatusException
     */
    public User UserIn(User user) {
        User found = userRepository.findByUsername(user.getUsername());//can be null if not found
        if(found ==null){// name does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Not a valid username"));
        }
        if(! user.getPassword().equals(found.getPassword())){// wrong password
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("wrong password"));
        }
        found.setStatus(UserStatus.ONLINE);

        user = userRepository.save(found);
        userRepository.flush();

        log.debug("User now online: ", user);

        return found;
    }

    /**
     * Changes userStatus of user to offline
     *
     * @param user
     * @throws ResponseStatusException
     */
    public void UserLogout(User user) {
        if(user !=null){// token has corresponding user
            user.setStatus(UserStatus.OFFLINE);

            user = userRepository.save(user);
            userRepository.flush();

            log.debug("User now offline: ", user);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found/invalid token");
        }
    }

    /**
     * Changes User information, if the input provided is valid and not null
     *
     * @param found
     * @param username
     * @param password
     * @param location
     * @param token
     * @throws ResponseStatusException
     */
    public void EditProfile(User found,String username, String password,String location, String token){
        if(found ==null){// id does not exist,   should never happen but...
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("user with userId was not found"));
        }
        if(! found.getToken().equals(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Not your Profile"));

        }
        //check if new name already exists
        User UserWithName = userRepository.findByUsername(username);
        if(! (UserWithName==null)&&!(found.equals(UserWithName))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Username taken/invalid"));
        }
        if(!(username==null)){
            found.setPassword(username);

            found = userRepository.save(found);
            userRepository.flush();

            log.debug("User changed Username: ", found);
        }
        if(!(password==null)){
            found.setUsername(password);

            found = userRepository.save(found);
            userRepository.flush();

            log.debug("User changed password: ", found);
        }
        if(!(location==null)){// this will change,  we need to check if valid location
            found.setLocation(location);

            found = userRepository.save(found);
            userRepository.flush();

            log.debug("User changed location: ", found);
        }
    }

//    /**
//     * Changes Username of User
//     *
//     * @param User
//     * @param NewName
//     */
//    public void ChangeUsername(User User, String NewName) {
//        User.setUsername(NewName);
//
//        User = userRepository.save(User);
//        userRepository.flush();
//
//        log.debug("User changed Username: ", User);
//    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        if (userByUsername != null) {//found an user with username
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the user could not be created!");
        }
    }
}

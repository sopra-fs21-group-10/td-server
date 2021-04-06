package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.WeatherDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
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
    public User userIn(User user) {
        User found = userRepository.findByUsername(user.getUsername());//can be null if not found
        if(found ==null){// name does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid username");
        }
        if(! user.getPassword().equals(found.getPassword())){// wrong password
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password");
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
    public void userLogout(User user) {
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
     * Changes User information, if the input provided is valid and not null, and token must match id
     *
     * @param found
     * @param username
     * @param password
     * @param location
     * @param token
     * @throws ResponseStatusException
     */
    public void editProfile(User found, String token, String username, String password, String location){
        if(found ==null){// id does not exist,   should never happen but...
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with userId was not found");
        }
        if(! found.getToken().equals(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not your Profile");

        }
        //check if new name already exists
        User UserWithName = userRepository.findByUsername(username);
        if((UserWithName!=null)&&!(found.equals(UserWithName))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username taken/invalid");
        }

        if(username!=null){
            found.setUsername(username);

            log.debug("User changed Username: ", found);
        }
        if(password!=null){
            found.setPassword(password);

            log.debug("User changed Password: ", found);
        }

        if(location!=null){// this will change,  we need to check if valid location
            try {
                // test if location exists by making a request
                URL jsonUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q="+location+"&appid=d9c0704e11e748296bd7ce40527678a5");//last part is the key

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                WeatherDTO jweather = mapper.readValue(jsonUrl, WeatherDTO.class);

                //change if location was found
                found.setLocation(location);

                log.debug("User changed location: ", found);
            }catch (Exception e){
                System.out.println(e);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid location or to many requests");
            }
        }
        // cant do that after every change or only the 1. will get saved
        userRepository.save(found);
        userRepository.flush();
    }

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

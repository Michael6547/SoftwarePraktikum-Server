package ch.uzh.ifi.seal.soprafs19.service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotExistingException extends RuntimeException{
    public PlayerNotExistingException(String message){
        super(message);
    }
}

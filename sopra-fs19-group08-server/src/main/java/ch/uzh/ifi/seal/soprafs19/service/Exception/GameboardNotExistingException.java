package ch.uzh.ifi.seal.soprafs19.service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GameboardNotExistingException extends RuntimeException {
    public GameboardNotExistingException(String message){
        super(message);
    }
}

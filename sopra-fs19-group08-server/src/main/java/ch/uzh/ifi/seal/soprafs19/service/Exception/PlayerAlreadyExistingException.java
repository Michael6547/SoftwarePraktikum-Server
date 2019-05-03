package ch.uzh.ifi.seal.soprafs19.service.Exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PlayerAlreadyExistingException extends RuntimeException {
    public PlayerAlreadyExistingException(String message){
        super(message);
    }
}

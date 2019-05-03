package ch.uzh.ifi.seal.soprafs19.service.Exception;

public class ActionNotPossibleException extends RuntimeException {
    public ActionNotPossibleException(String message) {
        super(message);
        this.printStackTrace();

    }
}

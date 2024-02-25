package Exception;

public class AlreadyTakenException extends ResponseException{
    public AlreadyTakenException(int statusCode, String message) {
        super(statusCode, message);
    }
}

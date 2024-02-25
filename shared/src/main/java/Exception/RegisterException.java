package Exception;

public class RegisterException extends ResponseException {

    public RegisterException(int statusCode, String message) {
        super(statusCode, message);
    }
}

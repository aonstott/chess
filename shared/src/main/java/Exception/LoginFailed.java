package Exception;

public class LoginFailed extends ResponseException{
    public LoginFailed(int statusCode, String message) {
        super(statusCode, message);
    }
}

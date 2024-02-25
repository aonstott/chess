package Exception;

public class UnauthorizedException extends ResponseException{
    public UnauthorizedException(int statusCode, String message) {
        super(statusCode, message);
    }
}

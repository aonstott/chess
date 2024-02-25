package Exception;

public class BadRequest extends ResponseException{
    public BadRequest(int statusCode, String message) {
        super(statusCode, message);
    }
}

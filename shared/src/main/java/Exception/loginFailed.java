package Exception;

public class loginFailed extends ResponseException{
    public loginFailed(int statusCode, String message) {
        super(statusCode, message);
    }
}

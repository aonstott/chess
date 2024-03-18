package client;

import java.util.Arrays;
import Exception.ResponseException;
import reqres.LoginRequest;
import reqres.LoginResult;
import server.ServerFacade;
import service.AuthData;
import service.UserData;

public class PreLoginClient {
    private final String serverURL;
    private int state = 0;

    private String auth = null;
    private final ServerFacade serverFacade;
    public PreLoginClient(String serverURL)
    {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> signIn(params);
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - Help
                - Quit
                - Login
                - Register
                """;
    }

    public String signIn(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest info = new LoginRequest(username, password);
            LoginResult res = serverFacade.login(info);
            this.auth = res.authToken();
            state = 1;
            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String getAuth() {
        return auth;
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3)
        {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            LoginResult res = serverFacade.register(user);
            this.auth = res.authToken();
            state = 1;
            return String.format("Registered user %s", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
